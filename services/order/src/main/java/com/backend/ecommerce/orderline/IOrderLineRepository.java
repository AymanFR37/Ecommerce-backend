package com.backend.ecommerce.orderline;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderLineRepository extends JpaRepository<OrderLine, Integer> {

    List<OrderLine> findAllByOrderId(Integer orderId);
}
