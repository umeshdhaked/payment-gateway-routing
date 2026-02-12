package org.platinumrx.pgrouting.repositories.jpa;

import org.platinumrx.pgrouting.dbo.OrderTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTransactionJpaRepository extends JpaRepository<OrderTransactionEntity, String> {
    OrderTransactionEntity findByOrderId(String orderId);
}
