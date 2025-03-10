package com.backend.ecommerce.product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IProductMapper {

    @Mapping(target = "category", expression = "java(Category.builder().id(request.categoryId()).build())")
    Product toProduct(ProductRequest request);

    @Mapping(target = "categoryId", source = "product.category.id")
    @Mapping(target = "categoryName", source = "product.category.name")
    @Mapping(target = "categoryDescription", source = "product.category.description")
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "productId", source = "product.id")
    ProductPurchaseResponse toProductPurchaseResponse(Product product, double quantity);

}
