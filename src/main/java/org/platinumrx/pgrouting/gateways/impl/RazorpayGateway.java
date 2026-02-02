package org.platinumrx.pgrouting.gateways.impl;

import org.platinumrx.pgrouting.models.PaymentGateways;
import org.platinumrx.pgrouting.gateways.PaymentGatewayService;
import org.springframework.stereotype.Service;

@Service
public class RazorpayGateway implements PaymentGatewayService {
    @Override
    public PaymentGateways getGatewayName() {
        return PaymentGateways.Razorpay;
    }

    @Override
    public Boolean checkHealth() {
        return true;
    }
}
