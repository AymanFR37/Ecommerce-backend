package com.backend.ecommerce.customer;

import com.backend.ecommerce.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {
    private final ICustomerRepository iCustomerRepository;
    private final ICustomerMapper iCustomerMapper;

    private Customer getCustomer(String customerId) {
        return iCustomerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Not found customer:: No customer found with the provided ID: %s", customerId)
                ));
    }

    @Override
    public String createCustomer(CustomerRequest request) {
        return Optional.of(request)
                .map(iCustomerMapper::toCustomer)
                .map(iCustomerRepository::save)
                .map(Customer::getId)
                .orElseThrow(() -> new IllegalStateException("Cannot create customer"));
    }

    @Override
    public void updateCustomer(CustomerRequest request) {
        Optional.of(request)
                .map(CustomerRequest::id)
                .map(this::getCustomer)
                .ifPresent(customer -> iCustomerMapper.update(request, customer));
    }

    @Override
    public List<CustomerResponse> findAllCustomers() {
        return iCustomerRepository.findAll().stream()
                .map(iCustomerMapper::fromCustomer)
                .toList();
    }

    @Override
    public Boolean existsById(String customerId) {
        return Optional.of(customerId)
                .map(iCustomerRepository::existsById)
                .orElse(false);
    }

    @Override
    public CustomerResponse findById(String customerId) {
        return iCustomerRepository.findById(customerId).map(iCustomerMapper::fromCustomer)
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Cannot update customer:: No customer found with the provided ID: %s", customerId)));
    }

    @Override
    public void deleteCustomer(String customerId) {
        Optional.of(customerId)
                .ifPresent(iCustomerRepository::deleteById);
    }
}
