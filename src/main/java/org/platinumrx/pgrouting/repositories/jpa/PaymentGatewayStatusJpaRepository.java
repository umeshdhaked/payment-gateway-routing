package org.platinumrx.pgrouting.repositories.jpa;

import org.platinumrx.pgrouting.dbo.GatewayStatusDetails;
import org.platinumrx.pgrouting.models.PaymentGateways;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentGatewayStatusJpaRepository extends JpaRepository<GatewayStatusDetails, Long> {
    Optional<GatewayStatusDetails> findByGateways(PaymentGateways gateway);
}
