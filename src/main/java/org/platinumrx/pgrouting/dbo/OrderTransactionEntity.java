package org.platinumrx.pgrouting.dbo;

import lombok.Data;
import org.platinumrx.pgrouting.models.PaymentGateways;

@Data
public class OrderTransactionEntity {
    private String orderId;
    private Double amount;
    private OrderTransactionStatus status;
    private PaymentGateways gateway;
    private String transactionId;
}
