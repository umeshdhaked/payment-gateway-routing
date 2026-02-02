package org.platinumrx.pgrouting.config;

import org.platinumrx.pgrouting.models.LoadDistributionConfiguration;
import org.platinumrx.pgrouting.models.PaymentGateways;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public LoadDistributionConfiguration loadDistributionConfiguration() {
        return new LoadDistributionConfiguration(Map.of(
                PaymentGateways.Razorpay, 50,
                PaymentGateways.PayU, 20,
                PaymentGateways.Cashfree, 30
        ));
    }
}
