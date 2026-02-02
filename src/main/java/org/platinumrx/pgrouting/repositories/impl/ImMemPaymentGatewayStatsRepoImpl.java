package org.platinumrx.pgrouting.repositories.impl;

import org.platinumrx.pgrouting.dbo.OrderTransactionStatus;
import org.platinumrx.pgrouting.dbo.PaymentStatEntity;
import org.platinumrx.pgrouting.models.PaymentGateways;
import org.platinumrx.pgrouting.repositories.PaymentGatewayStatsRepo;
import org.springframework.stereotype.Service;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ImMemPaymentGatewayStatsRepoImpl implements PaymentGatewayStatsRepo {

    private final Map<PaymentGateways, List<PaymentStatEntity>> statsMap = new ConcurrentHashMap<>();
    private final long windowDurationSeconds;

    public ImMemPaymentGatewayStatsRepoImpl(
            @Value("${gateway.stats.window-duration-seconds:900}") long windowDurationSeconds) {
        this.windowDurationSeconds = windowDurationSeconds;
    }

    @Override
    public void reportPaymentStatus(PaymentGateways gateway, OrderTransactionStatus status) {
        statsMap.computeIfAbsent(gateway, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(new PaymentStatEntity(gateway, Instant.now(), status));
    }

    @Override
    public Double getPaymentSuccessRate(PaymentGateways gateway) {
        List<PaymentStatEntity> attempts = statsMap.getOrDefault(gateway, new ArrayList<>());
        Instant threshold = Instant.now().minusSeconds(windowDurationSeconds);

        List<PaymentStatEntity> recentAttempts;
        synchronized (attempts) {
            recentAttempts = attempts.stream()
                    .filter(a -> a.getTimestamp().isAfter(threshold))
                    .toList();
        }

        if (recentAttempts.isEmpty()) {
            return 1.0; // Assume healthy if no data
        }

        long successCount = recentAttempts.stream()
                .filter(a -> a.getStatus() == OrderTransactionStatus.SUCCESS)
                .count();

        return (double) successCount / recentAttempts.size();
    }

}
