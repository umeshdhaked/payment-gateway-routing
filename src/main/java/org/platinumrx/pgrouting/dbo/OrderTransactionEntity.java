package org.platinumrx.pgrouting.dbo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.platinumrx.pgrouting.models.PaymentGateways;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "order_transactions")
public class OrderTransactionEntity {
    @Id
    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private OrderTransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "gateway", nullable = false, length = 50)
    private PaymentGateways gateway;

    @Column(name = "transaction_id")
    private String transactionId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
