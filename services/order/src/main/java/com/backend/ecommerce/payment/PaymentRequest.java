package com.backend.ecommerce.payment;

import com.backend.ecommerce.customer.CustomerResponse;
import com.backend.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
