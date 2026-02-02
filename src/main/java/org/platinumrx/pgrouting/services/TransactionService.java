package org.platinumrx.pgrouting.services;

import org.platinumrx.pgrouting.rto.*;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {

    TransactionInitiateResponse initiateTransaction(TransactionInitiateRequest request);

    TransactionCallbackResponse updatePaymentStatus(TransactionCallbackRequest transactionCallbackRequest);

}
