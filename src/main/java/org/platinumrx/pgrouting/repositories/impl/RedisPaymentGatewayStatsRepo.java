package org.platinumrx.pgrouting.repositories.impl;

import lombok.extern.slf4j.Slf4j;
import org.platinumrx.pgrouting.dbo.OrderTransactionStatus;
import org.platinumrx.pgrouting.models.PaymentGateways;
import org.platinumrx.pgrouting.repositories.PaymentGatewayStatsRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@Primary
@Slf4j
public class RedisPaymentGatewayStatsRepo implements PaymentGatewayStatsRepo {

    private final RedisTemplate<String, Object> redisTemplate;
    private final long windowDurationSeconds;

    private static final String KEY_PREFIX = "gateway:stats:";
    private static final String SUCCESS_SUFFIX = ":success";
    private static final String TOTAL_SUFFIX = ":total";

    public RedisPaymentGatewayStatsRepo(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${gateway.stats.window-duration-seconds:900}") long windowDurationSeconds) {
        this.redisTemplate = redisTemplate;
        this.windowDurationSeconds = windowDurationSeconds;
    }

    @Override
    public void reportPaymentStatus(PaymentGateways gateway, OrderTransactionStatus status) {
        try {
            long timestamp = Instant.now().toEpochMilli();
            String totalKey = KEY_PREFIX + gateway.name() + TOTAL_SUFFIX;

            // Add to total transactions sorted set
            redisTemplate.opsForZSet().add(totalKey, String.valueOf(timestamp), timestamp);

            // If successful, also add to success sorted set
            if (status == OrderTransactionStatus.SUCCESS) {
                String successKey = KEY_PREFIX + gateway.name() + SUCCESS_SUFFIX;
                redisTemplate.opsForZSet().add(successKey, String.valueOf(timestamp), timestamp);
            }

            log.debug("Reported payment status for gateway {}: {}", gateway, status);

            // Clean up old entries outside the window
            cleanupOldEntries(gateway);
        } catch (Exception e) {
            log.error("Error reporting payment status to Redis for gateway {}", gateway, e);
        }
    }

    @Override
    public Double getPaymentSuccessRate(PaymentGateways gateway) {
        try {
            // Clean up old entries first
            cleanupOldEntries(gateway);

            String totalKey = KEY_PREFIX + gateway.name() + TOTAL_SUFFIX;
            String successKey = KEY_PREFIX + gateway.name() + SUCCESS_SUFFIX;

            // Get counts within the time window
            long currentTime = Instant.now().toEpochMilli();
            long windowStart = currentTime - (windowDurationSeconds * 1000);

            Long totalCount = redisTemplate.opsForZSet().count(totalKey, windowStart, currentTime);
            Long successCount = redisTemplate.opsForZSet().count(successKey, windowStart, currentTime);

            if (totalCount == null || totalCount == 0) {
                log.debug("No data for gateway {}, assuming healthy (1.0)", gateway);
                return 1.0; // Assume healthy if no data
            }

            if (successCount == null) {
                successCount = 0L;
            }

            double rate = (double) successCount / totalCount;
            log.debug("Success rate for gateway {}: {}/{} = {}", gateway, successCount, totalCount, rate);

            return rate;
        } catch (Exception e) {
            log.error("Error getting payment success rate from Redis for gateway {}", gateway, e);
            return 1.0; // Assume healthy on error
        }
    }

    /**
     * Remove entries older than the time window to prevent unbounded growth
     */
    private void cleanupOldEntries(PaymentGateways gateway) {
        try {
            long currentTime = Instant.now().toEpochMilli();
            long windowStart = currentTime - (windowDurationSeconds * 1000);

            String totalKey = KEY_PREFIX + gateway.name() + TOTAL_SUFFIX;
            String successKey = KEY_PREFIX + gateway.name() + SUCCESS_SUFFIX;

            // Remove entries older than the window
            Long removedTotal = redisTemplate.opsForZSet().removeRangeByScore(totalKey, 0, windowStart);
            Long removedSuccess = redisTemplate.opsForZSet().removeRangeByScore(successKey, 0, windowStart);

            if (removedTotal != null && removedTotal > 0) {
                log.debug("Cleaned up {} old total entries for gateway {}", removedTotal, gateway);
            }
            if (removedSuccess != null && removedSuccess > 0) {
                log.debug("Cleaned up {} old success entries for gateway {}", removedSuccess, gateway);
            }
        } catch (Exception e) {
            log.error("Error cleaning up old entries for gateway {}", gateway, e);
        }
    }
}
