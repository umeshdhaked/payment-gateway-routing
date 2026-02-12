package org.platinumrx.pgrouting.repositories.impl;

import lombok.extern.slf4j.Slf4j;
import org.platinumrx.pgrouting.config.DataSourceContextHolder;
import org.platinumrx.pgrouting.dbo.GatewayStatusDetails;
import org.platinumrx.pgrouting.models.PaymentGatewayStatus;
import org.platinumrx.pgrouting.models.PaymentGateways;
import org.platinumrx.pgrouting.repositories.PaymentGatewayStatusRepo;
import org.platinumrx.pgrouting.repositories.jpa.PaymentGatewayStatusJpaRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
@Primary
@Slf4j
public class PostgresPaymentGatewayStatusRepo implements PaymentGatewayStatusRepo {

    private final PaymentGatewayStatusJpaRepository jpaRepository;

    public PostgresPaymentGatewayStatusRepo(PaymentGatewayStatusJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public void updateStatus(PaymentGateways gateway, PaymentGatewayStatus status) {
        try {
            DataSourceContextHolder.setDataSourceType(DataSourceContextHolder.DataSourceType.WRITE);
            log.info("Updating status for gateway {} to {}", gateway, status);

            GatewayStatusDetails details = jpaRepository.findByGateways(gateway)
                    .orElse(new GatewayStatusDetails(gateway, status, Instant.now()));

            details.setStatus(status);
            details.setLastUpdated(Instant.now());

            jpaRepository.save(details);
            log.info("Successfully updated status for gateway: {}", gateway);
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GatewayStatusDetails getStatus(PaymentGateways gateway) {
        try {
            DataSourceContextHolder.setDataSourceType(DataSourceContextHolder.DataSourceType.READ);
            log.info("Fetching status for gateway: {}", gateway);
            return jpaRepository.findByGateways(gateway)
                    .orElse(new GatewayStatusDetails(gateway, PaymentGatewayStatus.DISABLED, Instant.now()));
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }
}
