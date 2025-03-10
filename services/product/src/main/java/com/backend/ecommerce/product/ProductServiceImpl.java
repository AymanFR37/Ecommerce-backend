package com.backend.ecommerce.product;

import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final IProductRepository iProductRepository;
    private final IProductMapper iProductMapper;

    @Override
    public Integer createProduct(ProductRequest request) {
        return Optional.of(request)
                .map(iProductMapper::toProduct)
                .map(iProductRepository::save)
                .map(Product::getId)
                .orElseThrow(() -> new BusinessException("Product creation failed"));
    }

//    @Transactional(rollbackFor = ProductPurchaseException.class)
//    @Override
//    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> request) {
//        final var productIds = request.stream()
//                .map(ProductPurchaseRequest::productId)
//                .toList();
//        final var storedProducts = getStoredProducts(productIds);
//        validateProducts(storedProducts, productIds);
//        final var sortedRequest = request
//                .stream()
//                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
//                .toList();
//        final ArrayList<ProductPurchaseResponse> purchasedProducts = new ArrayList<>();
//        for (int i = 0; i < storedProducts.size(); i++) {
//            final var product = storedProducts.get(i);
//            final var productRequest = sortedRequest.get(i);
//            if (product.getAvailableQuantity() < productRequest.quantity()) {
//                throw new ProductPurchaseException("Insufficient stock quantity for product with ID:: " + productRequest.productId());
//            }
//            final var newAvailableQuantity = product.getAvailableQuantity() - productRequest.quantity();
//            product.setAvailableQuantity(newAvailableQuantity);
//            iProductRepository.save(product);
//            purchasedProducts.add(iProductMapper.toProductPurchaseResponse(product, productRequest.quantity()));
//        }
//        return purchasedProducts;
//    }
//


    @Transactional(rollbackFor = ProductPurchaseException.class)
    @Override
    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> request) {
        final var productIds = request.stream()
                .map(ProductPurchaseRequest::productId)
                .toList();

        final var storedProducts = getStoredProducts(productIds);
        validateProducts(storedProducts, productIds);

        return IntStream.range(0, storedProducts.size())
                .mapToObj(i -> processPurchase(storedProducts.get(i), request.get(i)))
                .toList();
    }

    private ProductPurchaseResponse processPurchase(Product storedProduct, ProductPurchaseRequest request) {
        if (storedProduct.getAvailableQuantity() < request.quantity()) {
            throw new ProductPurchaseException("Insufficient stock quantity for product with ID:: " + request.productId());
        }

        storedProduct.setAvailableQuantity(storedProduct.getAvailableQuantity() - request.quantity());
        iProductRepository.save(storedProduct);

        return iProductMapper.toProductPurchaseResponse(storedProduct, request.quantity());
    }

    private static void validateProducts(List<Product> storedProducts, List<Integer> productIds) {
        if (storedProducts.size() != productIds.size()) {
            throw new ProductPurchaseException("One or more products not found");
        }
    }

    private List<Product> getStoredProducts(List<Integer> productIds) {
        return iProductRepository.findAllByIdInOrderById(productIds);
    }


    @Override
    public ProductResponse findById(Integer productId) {
        return iProductRepository.findById(productId)
                .map(iProductMapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID:: " + productId));
    }

    @Override
    public List<ProductResponse> findAll() {
        return iProductRepository.findAll()
                .stream()
                .map(iProductMapper::toProductResponse)
                .toList();
    }
}
