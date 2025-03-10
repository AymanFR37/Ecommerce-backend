package com.backend.ecommerce.orderline;

import com.backend.ecommerce.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IOrderLineMapper {

    @Mapping(target = "order", expression = "java(Order.builder().id(request.orderId()).build())")
    OrderLine toOrderLine(OrderLineRequest request);

    OrderLineResponse toOrderLineResponse(OrderLine orderLine);

    default Order buildOrder(OrderLineRequest request) {
        return Order.builder()
                .id(request.orderId())
                .build();
    }

//    public OrderLine toOrderLine(OrderLineRequest request) {
//        return OrderLine.builder()
//                .productId(request.productId())
//                .order(
//                        Order.builder()
//                                .id(request.orderId())
//                                .build()
//                )
//                .quantity(request.quantity())
//                .build();
//    }
//
//    public OrderLineResponse toOrderLineResponse(OrderLine orderLine) {
//        return new OrderLineResponse(
//                orderLine.getId(),
//                orderLine.getQuantity()
//        );
//    }
}
