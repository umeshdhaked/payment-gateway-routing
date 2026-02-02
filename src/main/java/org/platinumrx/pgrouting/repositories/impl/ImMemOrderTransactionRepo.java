package org.platinumrx.pgrouting.repositories.impl;

import lombok.extern.slf4j.Slf4j;
import org.platinumrx.pgrouting.dbo.OrderTransactionEntity;
import org.platinumrx.pgrouting.dbo.OrderTransactionStatus;
import org.platinumrx.pgrouting.repositories.OrderTransactionRepo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Slf4j
public class ImMemOrderTransactionRepo implements OrderTransactionRepo {

    private final Map<String, OrderTransactionEntity> orderTransactionMap = new HashMap<>();

    @Override
    public OrderTransactionEntity save(OrderTransactionEntity orderTransactionEntity) {
        orderTransactionEntity.setStatus(OrderTransactionStatus.PENDING);
        log.info("Saving order transaction {}", orderTransactionEntity);
        orderTransactionMap.put(orderTransactionEntity.getOrderId(), orderTransactionEntity);
        return orderTransactionMap.get(orderTransactionEntity.getOrderId());
    }

    @Override
    public OrderTransactionEntity findByOrderId(String orderId) {
        log.info("Fetching order transaction for orderId {}", orderId);
        return orderTransactionMap.get(orderId);
    }

    @Override
    public OrderTransactionEntity updateStatus(String orderId, OrderTransactionStatus status) {
        OrderTransactionEntity orderTransactionEntity = orderTransactionMap.get(orderId);
        orderTransactionEntity.setStatus(status);
        orderTransactionMap.put(orderId, orderTransactionEntity);
        log.info("Updated status of order {} to {}", orderId, status);
        return orderTransactionEntity;
    }
}
