package org.platinumrx.pgrouting.services.impl;

import org.platinumrx.pgrouting.gateways.PaymentGatewayFactory;
import org.platinumrx.pgrouting.gateways.PaymentGatewayService;
import org.platinumrx.pgrouting.dbo.GatewayStatusDetails;
import org.platinumrx.pgrouting.models.LoadDistributionConfiguration;
import org.platinumrx.pgrouting.models.PaymentGatewayStatus;
import org.platinumrx.pgrouting.models.PaymentGateways;
import org.platinumrx.pgrouting.repositories.PaymentGatewayStatusRepo;
import org.platinumrx.pgrouting.services.GatewaySelectionStrategy;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;
import java.util.Map;

@Component
public class DefaultGatewaySelectionStrategy implements GatewaySelectionStrategy {

    private final LoadDistributionConfiguration loadDistributionConfiguration;
    private final PaymentGatewayStatusRepo paymentGatewayStatusRepo;
    private final PaymentGatewayFactory paymentGatewayFactory;
    private final long tempDisableDurationSeconds;

    public DefaultGatewaySelectionStrategy(
            PaymentGatewayFactory paymentGatewayFactory,
            PaymentGatewayStatusRepo paymentGatewayStatusRepo,
            LoadDistributionConfiguration loadDistributionConfiguration,
            @Value("${gateway.temp-disable-duration-seconds:1800}") long tempDisableDurationSeconds) {
        this.paymentGatewayStatusRepo = paymentGatewayStatusRepo;
        this.paymentGatewayFactory = paymentGatewayFactory;
        this.loadDistributionConfiguration = loadDistributionConfiguration;
        this.tempDisableDurationSeconds = tempDisableDurationSeconds;
    }

    @Override
    public PaymentGatewayService getGateway() {

        Map<PaymentGateways, Integer> distribution = loadDistributionConfiguration.getPaymentMethodDistribution();

        if (distribution == null || distribution.isEmpty()) {
            throw new RuntimeException("No payment gateway distribution configured");
        }

        Map<PaymentGateways, Integer> activeGateways = new HashMap<>();
        int totalWeight = 0;

        for (Map.Entry<PaymentGateways, Integer> entry : distribution.entrySet()) {
            PaymentGateways gateway = entry.getKey();
            Integer weight = entry.getValue();

            GatewayStatusDetails statusDetails = paymentGatewayStatusRepo.getStatus(gateway);
            boolean isEnabled = statusDetails.getStatus() == PaymentGatewayStatus.ENABLED;

            if (!isEnabled && statusDetails.getStatus() == PaymentGatewayStatus.TEMP_DISABLED) {
                if (statusDetails.getLastUpdated().plusSeconds(tempDisableDurationSeconds)
                        .isBefore(java.time.Instant.now())) {
                    isEnabled = true;
                    paymentGatewayStatusRepo.updateStatus(gateway, PaymentGatewayStatus.ENABLED);
                }
            }

            Boolean isAvailable = paymentGatewayFactory.getPaymentGatewayService(gateway).checkHealth();

            if (isEnabled && isAvailable) {
                if (weight != null && weight > 0) {
                    activeGateways.put(gateway, weight);
                    totalWeight += weight;
                }
            }
        }

        if (totalWeight == 0) {
            throw new RuntimeException("No healthy payment gateways available");
        }

        int randomWeight = new java.util.Random().nextInt(totalWeight);
        int currentWeight = 0;

        for (Map.Entry<PaymentGateways, Integer> entry : activeGateways.entrySet()) {
            currentWeight += entry.getValue();
            if (randomWeight < currentWeight) {
                return paymentGatewayFactory.getPaymentGatewayService(entry.getKey());
            }
        }

        throw new RuntimeException("No active gateway found based on Default Gateway Selection Strategy");
    }

}
