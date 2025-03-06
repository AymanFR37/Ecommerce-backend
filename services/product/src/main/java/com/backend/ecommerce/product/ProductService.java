package com.backend.ecommerce.product;

import java.util.List;

public interface ProductService {
    Integer createProduct(ProductRequest request);

    List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> request);

    ProductResponse findById(Integer productId);

    List<ProductResponse> findAll();
}
