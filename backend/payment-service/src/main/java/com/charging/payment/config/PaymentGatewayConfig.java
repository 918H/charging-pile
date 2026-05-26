package com.charging.payment.config;

import com.charging.payment.service.PaymentGatewayService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentGatewayConfig {

    @Bean
    public PaymentGatewayService paymentGatewayService() {
        return new MockPaymentGatewayService();
    }
}
