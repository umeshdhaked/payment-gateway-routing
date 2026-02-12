package org.platinumrx.pgrouting.repositories.impl;

import lombok.extern.slf4j.Slf4j;
import org.platinumrx.pgrouting.config.DataSourceContextHolder;
import org.platinumrx.pgrouting.dbo.OrderTransactionEntity;
import org.platinumrx.pgrouting.dbo.OrderTransactionStatus;
import org.platinumrx.pgrouting.repositories.OrderTransactionRepo;
import org.platinumrx.pgrouting.repositories.jpa.OrderTransactionJpaRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Primary
@Slf4j
public class PostgresOrderTransactionRepo implements OrderTransactionRepo {

    private final OrderTransactionJpaRepository jpaRepository;

    public PostgresOrderTransactionRepo(OrderTransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public OrderTransactionEntity save(OrderTransactionEntity orderTransactionEntity) {
        try {
            DataSourceContextHolder.setDataSourceType(DataSourceContextHolder.DataSourceType.WRITE);
            orderTransactionEntity.setStatus(OrderTransactionStatus.PENDING);
            log.info("Saving order transaction to database: {}", orderTransactionEntity.getOrderId());
            OrderTransactionEntity saved = jpaRepository.save(orderTransactionEntity);
            log.info("Successfully saved order transaction: {}", saved.getOrderId());
            return saved;
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderTransactionEntity findByOrderId(String orderId) {
        try {
            DataSourceContextHolder.setDataSourceType(DataSourceContextHolder.DataSourceType.READ);
            log.info("Fetching order transaction from database: {}", orderId);
            return jpaRepository.findByOrderId(orderId);
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }

    @Override
    @Transactional
    public OrderTransactionEntity updateStatus(String orderId, OrderTransactionStatus status) {
        try {
            DataSourceContextHolder.setDataSourceType(DataSourceContextHolder.DataSourceType.WRITE);
            log.info("Updating status for order {} to {}", orderId, status);
            OrderTransactionEntity entity = jpaRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
            entity.setStatus(status);
            OrderTransactionEntity updated = jpaRepository.save(entity);
            log.info("Successfully updated status for order: {}", orderId);
            return updated;
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }
}
