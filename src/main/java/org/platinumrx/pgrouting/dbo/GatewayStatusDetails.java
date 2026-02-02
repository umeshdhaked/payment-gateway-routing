package org.platinumrx.pgrouting.dbo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platinumrx.pgrouting.models.PaymentGatewayStatus;
import org.platinumrx.pgrouting.models.PaymentGateways;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GatewayStatusDetails {
    private PaymentGateways gateways;
    private PaymentGatewayStatus status;
    private Instant lastUpdated;
}
