package org.platinumrx.pgrouting.gateways;

import org.platinumrx.pgrouting.models.PaymentGateways;

public interface PaymentGatewayService {
    PaymentGateways getGatewayName();
    Boolean checkHealth();
}
