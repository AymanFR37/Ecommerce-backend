/**
 * This class is a gRPC client for interacting with the ProductService.
 * It uses a blocking stub to make synchronous gRPC calls to the ProductService.
 */
package com.backend.ecommerce.product;

import com.backend.grpcinterface.proto.ProductListResponse;
import com.backend.grpcinterface.proto.ProductRequest;
import com.backend.grpcinterface.proto.ProductResponse;
import com.backend.grpcinterface.proto.ProductServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
/**
 * A gRPC client for fetching product information from the ProductService.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductGrpcClient {

    /**
     * The blocking stub for making synchronous gRPC calls to the ProductService.
     */
    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceStub;
    /**
     * The blocking stub for making synchronous gRPC calls to the ProductService.
     */
    private final ManagedChannel channel;

    /**
     * Constructs a new ProductGrpcClient with a managed channel and blocking stub.
     */
    public ProductGrpcClient() {
        // Create a managed channel to the gRPC server at localhost:8085
        this.channel = ManagedChannelBuilder.forAddress("localhost", 8085)
                .usePlaintext() // Use plaintext communication (no SSL/TLS)
                .keepAliveTime(30, TimeUnit.SECONDS) // Send keep-alive pings every 30 seconds
                .keepAliveTimeout(10, TimeUnit.SECONDS) // Timeout for keep-alive pings
                .maxInboundMessageSize(10 * 1024 * 1024) // Set max inbound message size to 10 MB
                .build();
        // Create a blocking stub with gzip compression
        productServiceStub = ProductServiceGrpc.newBlockingStub(channel)
                .withCompression("gzip");
    }

    /**
     * Fetches all products from the ProductService.
     *
     * @return a list of PurchaseResponse objects representing the products.
     */
    public List<PurchaseResponse> getAllProducts() {
        log.info("in product grpc client");
        try {
            final ProductListResponse response = productServiceStub
                    .withDeadlineAfter(1000, TimeUnit.MILLISECONDS)
                    .getAllProducts(ProductRequest.newBuilder()
                            .build());
            return convertToJson(response.getProductsList());
        } catch (StatusRuntimeException e) {
            log.info("RPC failed status: {}", e.getStatus());
            return Collections.emptyList();
        }
    }

    /**
     * Converts a list of ProductResponse objects to a list of PurchaseResponse objects.
     *
     * @param products the list of ProductResponse objects to convert.
     * @return a list of PurchaseResponse objects.
     */
    private List<PurchaseResponse> convertToJson(List<ProductResponse> products) {
        return products.stream()
                .map(product -> PurchaseResponse.builder()
                        .productId(product.getProductId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(BigDecimal.valueOf(product.getPrice()))
                        .quantity(product.getQuantity())
                        .build())
                .toList();
    }
}