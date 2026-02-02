package org.platinumrx.pgrouting.rto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionInitiateRequest {
    private String orderId;
    public Double amount;
    private PaymentInstruction paymentInstruction;
}
