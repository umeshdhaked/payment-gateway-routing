package org.platinumrx.pgrouting.repositories;

import org.platinumrx.pgrouting.dbo.OrderTransactionStatus;
import org.platinumrx.pgrouting.models.PaymentGateways;

public interface PaymentGatewayStatsRepo {
    void reportPaymentStatus(PaymentGateways gateway, OrderTransactionStatus status);
    Double getPaymentSuccessRate(PaymentGateways gateway);
}
