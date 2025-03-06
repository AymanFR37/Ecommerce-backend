package com.backend.ecommerce.kafka;

import com.backend.ecommerce.customer.CustomerResponse;
import com.backend.ecommerce.order.PaymentMethod;
import com.backend.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}
