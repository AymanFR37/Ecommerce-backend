package com.backend.ecommerce.customer;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ICustomerRepository extends MongoRepository<Customer, String> {
}
