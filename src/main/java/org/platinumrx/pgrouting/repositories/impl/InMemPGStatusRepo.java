package org.platinumrx.pgrouting.repositories.impl;

import org.platinumrx.pgrouting.dbo.GatewayStatusDetails;
import org.platinumrx.pgrouting.models.PaymentGatewayStatus;
import org.platinumrx.pgrouting.models.PaymentGateways;
import org.platinumrx.pgrouting.repositories.PaymentGatewayStatusRepo;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemPGStatusRepo implements PaymentGatewayStatusRepo {

    private final Map<PaymentGateways, GatewayStatusDetails> statusMap = new HashMap<>();

    public InMemPGStatusRepo() {
        statusMap.put(PaymentGateways.Razorpay, new GatewayStatusDetails(PaymentGateways.Razorpay,PaymentGatewayStatus.ENABLED, Instant.now()));
        statusMap.put(PaymentGateways.PayU, new GatewayStatusDetails(PaymentGateways.PayU,PaymentGatewayStatus.ENABLED, Instant.now()));
        statusMap.put(PaymentGateways.Cashfree, new GatewayStatusDetails(PaymentGateways.Cashfree,PaymentGatewayStatus.ENABLED, Instant.now()));
    }

    @Override
    public void updateStatus(PaymentGateways gateway, PaymentGatewayStatus status) {
        GatewayStatusDetails current = statusMap.get(gateway);
        if (current == null || current.getStatus() != status) {
            statusMap.put(gateway, new GatewayStatusDetails(gateway, status, Instant.now()));
        }
    }

    @Override
    public GatewayStatusDetails getStatus(PaymentGateways gateway) {
        return statusMap.getOrDefault(gateway, new GatewayStatusDetails(gateway,PaymentGatewayStatus.DISABLED, Instant.now()));
    }
}
