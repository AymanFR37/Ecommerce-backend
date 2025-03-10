/**
 * This class implements the gRPC service for managing products.
 */
package com.backend.ecommerce.grpc;

import com.backend.ecommerce.product.IProductRepository;
import com.backend.grpcinterface.proto.ProductListResponse;
import com.backend.grpcinterface.proto.ProductRequest;
import com.backend.grpcinterface.proto.ProductResponse;
import com.backend.grpcinterface.proto.ProductServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import lombok.RequiredArgsConstructor;

/**
 * gRPC service implementation for product-related operations.
 */
@GRpcService
@RequiredArgsConstructor
@Slf4j
public class ProductService extends ProductServiceGrpc.ProductServiceImplBase {

    private final IProductRepository repository;

    /**
     * Retrieves all products and sends them in the response.
     *
     * @param request           the request object (not used in this method)
     * @param responseObserver  the observer to send the response
     */
    @Override
    public void getAllProducts(ProductRequest request, StreamObserver<ProductListResponse> responseObserver) {
        log.info("in product service");
        final var products = repository.findAll();
        final var response = ProductListResponse.newBuilder();
        products.forEach(product -> response.addProducts(ProductResponse.newBuilder()
                .setProductId(product.getId())
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setPrice(product.getPrice().doubleValue())
                .setQuantity(product.getAvailableQuantity())
                .build()));
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
