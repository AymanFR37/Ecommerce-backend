package com.backend.ecommerce.product;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PurchaseResponse(
        Integer productId,
        String name,
        String description,
        BigDecimal price,
        double quantity
) {
}
