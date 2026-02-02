package org.platinumrx.pgrouting.models;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoadDistributionConfiguration {
    Map<PaymentGateways, Integer> paymentMethodDistribution;
}
