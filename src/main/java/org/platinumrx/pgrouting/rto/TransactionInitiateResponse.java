package org.platinumrx.pgrouting.rto;

import lombok.*;
import org.platinumrx.pgrouting.models.PaymentGateways;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class TransactionInitiateResponse {
    private String transactionId;
    private String orderId;
    private PaymentGateways gatewayName;
}
