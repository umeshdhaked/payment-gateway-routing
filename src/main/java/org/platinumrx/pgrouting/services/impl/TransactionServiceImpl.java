package org.platinumrx.pgrouting.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.platinumrx.pgrouting.dbo.OrderTransactionEntity;
import org.platinumrx.pgrouting.dbo.OrderTransactionStatus;
import org.platinumrx.pgrouting.gateways.PaymentGatewayService;
import org.platinumrx.pgrouting.repositories.OrderTransactionRepo;
import org.platinumrx.pgrouting.repositories.PaymentGatewayStatsRepo;
import org.platinumrx.pgrouting.repositories.PaymentGatewayStatusRepo;
import org.platinumrx.pgrouting.models.PaymentGatewayStatus;
import org.platinumrx.pgrouting.rto.*;
import org.platinumrx.pgrouting.services.TransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

        private final DefaultGatewaySelectionStrategy gatewaySelectionStrategy;
        private final PaymentGatewayStatsRepo paymentGatewayStatsRepo;
        private final OrderTransactionRepo orderTransactionRepo;
        private final PaymentGatewayStatusRepo paymentGatewayStatusRepo;
        private final Double minSuccessThreshold;

        public TransactionServiceImpl(DefaultGatewaySelectionStrategy gatewaySelectionStrategy,
                        PaymentGatewayStatsRepo paymentGatewayStatsRepo,
                        OrderTransactionRepo orderTransactionRepo,
                        PaymentGatewayStatusRepo paymentGatewayStatusRepo,
                        @Value("${gateway.min-success-threshold:0.9}") Double minSuccessThreshold) {

                this.gatewaySelectionStrategy = gatewaySelectionStrategy;
                this.paymentGatewayStatsRepo = paymentGatewayStatsRepo;
                this.orderTransactionRepo = orderTransactionRepo;
                this.paymentGatewayStatusRepo = paymentGatewayStatusRepo;
                this.minSuccessThreshold = minSuccessThreshold;
        }

        public TransactionInitiateResponse initiateTransaction(TransactionInitiateRequest request) {
                String transactionId = UUID.randomUUID().toString();

                PaymentGatewayService gateway = gatewaySelectionStrategy.getGateway();

                OrderTransactionEntity orderTransactionEntity = new OrderTransactionEntity();
                orderTransactionEntity.setOrderId(request.getOrderId());
                orderTransactionEntity.setStatus(OrderTransactionStatus.PENDING);
                orderTransactionEntity.setGateway(gateway.getGatewayName());
                orderTransactionEntity.setAmount(request.getAmount());
                orderTransactionEntity.setTransactionId(transactionId);
                OrderTransactionEntity resp = orderTransactionRepo.save(orderTransactionEntity);

                log.info("Initiating transaction for gateway: {}", gateway.getGatewayName());

                return TransactionInitiateResponse.builder()
                                .orderId(resp.getOrderId())
                                .transactionId(resp.getTransactionId())
                                .gatewayName(resp.getGateway())
                                .build();
        }

        public TransactionCallbackResponse updatePaymentStatus(TransactionCallbackRequest transactionCallbackRequest) {

                OrderTransactionStatus transactionStatus = TransactionStatus.SUCCESS
                                .equals(transactionCallbackRequest.getStatus())
                                                ? OrderTransactionStatus.SUCCESS
                                                : OrderTransactionStatus.FAILURE;

                OrderTransactionEntity orderTransactionEntity = orderTransactionRepo
                                .updateStatus(transactionCallbackRequest.getOrderId(), transactionStatus);

                paymentGatewayStatsRepo.reportPaymentStatus(transactionCallbackRequest.getGateway(), transactionStatus);

                Double successRate = paymentGatewayStatsRepo
                                .getPaymentSuccessRate(transactionCallbackRequest.getGateway());
                if (transactionStatus.equals(OrderTransactionStatus.FAILURE) && successRate < minSuccessThreshold) {
                        log.warn("Gateway {} success rate is {}, disabling it.",
                                        transactionCallbackRequest.getGateway(),
                                        successRate);
                        paymentGatewayStatusRepo.updateStatus(transactionCallbackRequest.getGateway(),
                                        PaymentGatewayStatus.TEMP_DISABLED);
                }

                return TransactionCallbackResponse.builder().transactionId(orderTransactionEntity.getTransactionId())
                                .build();
        }

}
