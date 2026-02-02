package org.platinumrx.pgrouting.rto;

import lombok.Getter;

@Getter
public enum TransactionStatus {

    SUCCESS ("success"),
    FAILURE ("failure");

    private final String status;

    TransactionStatus(String status) {
        this.status = status;
    }

}
