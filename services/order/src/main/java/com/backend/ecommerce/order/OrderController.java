/**
 * This class is a REST controller for managing orders.
 */
package com.backend.ecommerce.order;

import java.util.List;

import com.backend.ecommerce.product.ProductGrpcClient;
import com.backend.ecommerce.product.PurchaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;
    private final ProductGrpcClient productGrpcClient;

    /**
     * Creates a new order.
     *
     * @param request the order request containing order details.
     * @return the ID of the created order.
     */
    @PostMapping
    public ResponseEntity<Integer> createOrder(
            @RequestBody @Valid OrderRequest request
    ) {
        return ResponseEntity.ok(service.createOrder(request));
    }

    /**
     * Retrieves all orders.
     *
     * @return a list of all orders.
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() {
        return ResponseEntity.ok(service.findAllOrders());
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the ID of the order to retrieve.
     * @return the order with the specified ID.
     */
    @GetMapping("/{order-id}")
    public ResponseEntity<OrderResponse> findById(
            @PathVariable("order-id") Integer orderId
    ) {
        return ResponseEntity.ok(service.findById(orderId));
    }

    /**
     * Fetches all products from the ProductService using gRPC.
     *
     * @return a list of all products.
     */
    @GetMapping("/products")
    public ResponseEntity<List<PurchaseResponse>> fetchAllProducts() {
        return ResponseEntity.ok(productGrpcClient.getAllProducts());
    }
}
