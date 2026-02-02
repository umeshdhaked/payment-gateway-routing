package org.platinumrx.pgrouting.job;

import org.platinumrx.pgrouting.dbo.GatewayStatusDetails;
import org.platinumrx.pgrouting.models.PaymentGatewayStatus;
import org.platinumrx.pgrouting.models.PaymentGateways;
import org.platinumrx.pgrouting.repositories.PaymentGatewayStatsRepo;
import org.platinumrx.pgrouting.repositories.PaymentGatewayStatusRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PaymentGatewaysSuccessRateJob {

    private final PaymentGatewayStatusRepo paymentGatewayStatusRepo;
    private final PaymentGatewayStatsRepo paymentGatewayStatsRepo;

    public PaymentGatewaysSuccessRateJob(
            PaymentGatewayStatusRepo paymentGatewayStatusRepo,
            PaymentGatewayStatsRepo paymentGatewayStatsRepo) {
        this.paymentGatewayStatusRepo = paymentGatewayStatusRepo;
        this.paymentGatewayStatsRepo = paymentGatewayStatsRepo;
    }

//    @Scheduled(fixedRate = 1000)
    public void checkSuccessRate() {
        Arrays.stream(PaymentGateways.values()).forEach(pg -> {
            GatewayStatusDetails currentStatus = paymentGatewayStatusRepo.getStatus(pg);

            if (currentStatus.getStatus() == PaymentGatewayStatus.TEMP_DISABLED) {
                if (currentStatus.getLastUpdated().plusSeconds(30 * 60).isBefore(java.time.Instant.now())) {
                    paymentGatewayStatusRepo.updateStatus(pg, PaymentGatewayStatus.ENABLED);
                }
            } else if (currentStatus.getStatus() == PaymentGatewayStatus.ENABLED) {
                Double successRate = paymentGatewayStatsRepo.getPaymentSuccessRate(pg);
                if (successRate != null && successRate < 0.9) {
                    paymentGatewayStatusRepo.updateStatus(pg, PaymentGatewayStatus.TEMP_DISABLED);
                }
            }
        });

    }
}
