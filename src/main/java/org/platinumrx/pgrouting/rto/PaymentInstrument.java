package org.platinumrx.pgrouting.rto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PaymentInstrument {
    private PaymentMethod type;
    @JsonProperty("card_number") private String cardNumber;
    private Instant expiry;
}
