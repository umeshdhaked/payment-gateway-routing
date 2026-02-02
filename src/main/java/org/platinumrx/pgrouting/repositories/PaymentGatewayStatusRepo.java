package org.platinumrx.pgrouting.repositories;

import org.platinumrx.pgrouting.dbo.GatewayStatusDetails;
import org.platinumrx.pgrouting.models.PaymentGatewayStatus;
import org.platinumrx.pgrouting.models.PaymentGateways;

public interface PaymentGatewayStatusRepo {
    void updateStatus(PaymentGateways gateway, PaymentGatewayStatus status);

    GatewayStatusDetails getStatus(PaymentGateways gateway);
}
