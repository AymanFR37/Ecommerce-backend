package com.backend.ecommerce.customer;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ICustomerMapper {

    Customer toCustomer(CustomerRequest request);

    CustomerResponse fromCustomer(Customer customer);

    void update(CustomerRequest request, @MappingTarget Customer customer);
}
