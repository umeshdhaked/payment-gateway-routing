package org.platinumrx.pgrouting.rto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionCallbackRequest {
    private String orderId;
    private TransactionStatus status;
    private PaymentGateway gateway;
    private String reason;
}
