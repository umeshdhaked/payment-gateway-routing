package org.platinumrx.pgrouting.controllers;

import lombok.extern.slf4j.Slf4j;
import org.platinumrx.pgrouting.rto.TransactionCallbackRequest;
import org.platinumrx.pgrouting.rto.TransactionCallbackResponse;
import org.platinumrx.pgrouting.rto.TransactionInitiateRequest;
import org.platinumrx.pgrouting.rto.TransactionInitiateResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/transactions")
public class TransactionController {

    @PostMapping("/initiate")
    public ResponseEntity<TransactionInitiateResponse> initiateTransaction(@RequestBody TransactionInitiateRequest request,
                                                                           @RequestHeader HttpHeaders headers) {

        log.info("Transaction Request received -> request: {}, headers: {} ", request, headers);

        return ResponseEntity.ok(new TransactionInitiateResponse());
    }

    @PostMapping("/callback")
    public ResponseEntity<TransactionCallbackResponse> transactionCallback(@RequestBody TransactionCallbackRequest transactionCallbackRequest,
                                                                           @RequestHeader HttpHeaders headers) {
        log.info("Callback Request received -> request: {}, headers: {} ", transactionCallbackRequest, headers);

        return ResponseEntity.ok(new TransactionCallbackResponse());
    }

}
