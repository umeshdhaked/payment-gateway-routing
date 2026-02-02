package org.platinumrx.pgrouting.rto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class TransactionCallbackResponse {
    private String transactionId;
}
