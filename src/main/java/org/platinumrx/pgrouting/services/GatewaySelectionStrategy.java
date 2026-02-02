package org.platinumrx.pgrouting.services;

import org.platinumrx.pgrouting.gateways.PaymentGatewayService;
import org.platinumrx.pgrouting.models.PaymentGateways;

public interface GatewaySelectionStrategy {
    PaymentGatewayService getGateway();
}
