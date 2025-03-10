package com.backend.ecommerce.order;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IOrderMapper {

    @Mapping(target = "totalAmount", source = "amount")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "orderLines", ignore = true)
    Order toOrder(OrderRequest request);

    @Mapping(target = "amount", source = "totalAmount")
    OrderResponse fromOrder(Order order);
}