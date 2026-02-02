package org.platinumrx.pgrouting.controllers;

import lombok.extern.slf4j.Slf4j;
import org.platinumrx.pgrouting.models.PaymentGateways;
import org.platinumrx.pgrouting.rto.*;
import org.platinumrx.pgrouting.services.TransactionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/transactions")
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<TransactionInitiateResponse> initiateTransaction(@RequestBody TransactionInitiateRequest request,
                                                                           @RequestHeader HttpHeaders headers) {

        log.info("Transaction Request received -> request: {}, headers: {} ", request, headers);

        TransactionInitiateResponse transactionInitiateResponse = transactionService.initiateTransaction(request);

        return ResponseEntity.ok(transactionInitiateResponse);
    }

    @PostMapping("/callback")
    public ResponseEntity<TransactionCallbackResponse> transactionCallback(@RequestBody TransactionCallbackRequest transactionCallbackRequest,
                                                                           @RequestHeader HttpHeaders headers) {
        log.info("Callback Request received -> request: {}, headers: {} ", transactionCallbackRequest, headers);

        TransactionCallbackResponse transactionCallbackResponse
                = transactionService.updatePaymentStatus(transactionCallbackRequest);

        return ResponseEntity.ok(transactionCallbackResponse);
    }

}
