package org.platinumrx.pgrouting.dbo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platinumrx.pgrouting.models.PaymentGatewayStatus;
import org.platinumrx.pgrouting.models.PaymentGateways;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment_gateway_status")
public class GatewayStatusDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "gateway", nullable = false, unique = true, length = 50)
    private PaymentGateways gateways;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private PaymentGatewayStatus status;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    public GatewayStatusDetails(PaymentGateways gateways, PaymentGatewayStatus status, Instant lastUpdated) {
        this.gateways = gateways;
        this.status = status;
        this.lastUpdated = lastUpdated;
    }
}
