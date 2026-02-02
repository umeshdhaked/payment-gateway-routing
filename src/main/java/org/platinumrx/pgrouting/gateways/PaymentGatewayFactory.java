package org.platinumrx.pgrouting.gateways;

import org.platinumrx.pgrouting.models.PaymentGateways;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentGatewayFactory {

    private final Map<PaymentGateways, PaymentGatewayService> paymentGatewayServiceMap;

    public PaymentGatewayFactory(List<PaymentGatewayService> paymentGatewayServices) {
        this.paymentGatewayServiceMap = paymentGatewayServices.stream()
                .collect(Collectors.toMap(PaymentGatewayService::getGatewayName, Function.identity()));
    }

    public PaymentGatewayService getPaymentGatewayService(PaymentGateways gateway) {
        return paymentGatewayServiceMap.get(gateway);
    }

}
