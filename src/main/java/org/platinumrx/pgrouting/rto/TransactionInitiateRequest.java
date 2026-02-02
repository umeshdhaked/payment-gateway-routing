package org.platinumrx.pgrouting.rto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TransactionInitiateRequest {
    @JsonProperty("order_id") private String orderId;
    public Double amount;
    @JsonProperty("payment_instrument") private PaymentInstrument paymentInstrument;
}
