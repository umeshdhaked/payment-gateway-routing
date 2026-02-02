package org.platinumrx.pgrouting.rto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.platinumrx.pgrouting.models.PaymentGateways;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TransactionCallbackRequest {
    @JsonProperty("order_id")
    private String orderId;
    private TransactionStatus status;
    private PaymentGateways gateway;
    private String reason;
}
