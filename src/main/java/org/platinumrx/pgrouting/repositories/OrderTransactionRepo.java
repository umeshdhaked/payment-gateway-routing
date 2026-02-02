package org.platinumrx.pgrouting.repositories;

import org.platinumrx.pgrouting.dbo.OrderTransactionEntity;
import org.platinumrx.pgrouting.dbo.OrderTransactionStatus;

public interface OrderTransactionRepo {
    OrderTransactionEntity save(OrderTransactionEntity orderTransactionEntity);
    OrderTransactionEntity findByOrderId(String orderId);
    OrderTransactionEntity updateStatus(String orderId, OrderTransactionStatus status);
}
