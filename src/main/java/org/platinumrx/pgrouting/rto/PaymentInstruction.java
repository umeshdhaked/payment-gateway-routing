package org.platinumrx.pgrouting.rto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentInstruction {
    private PaymentMethod type;
    private String cardNumber;
    private Instant expiry;
}
