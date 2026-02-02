package org.platinumrx.pgrouting.dbo;

import lombok.Data;
import org.platinumrx.pgrouting.models.PaymentGateways;

import java.time.Instant;

@Data
public class PaymentStatEntity {
    PaymentGateways gateway;
    Instant timestamp;
    OrderTransactionStatus status;

    public PaymentStatEntity(PaymentGateways gateway, Instant timestamp, OrderTransactionStatus status) {
        this.gateway = gateway;
        this.timestamp = timestamp;
        this.status = status;
    }
}