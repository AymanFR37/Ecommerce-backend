package com.backend.ecommerce.order;

import com.backend.ecommerce.customer.ICustomerClient;
import com.backend.ecommerce.customer.CustomerResponse;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.kafka.OrderConfirmation;
import com.backend.ecommerce.kafka.OrderProducer;
import com.backend.ecommerce.orderline.OrderLineRequest;
import com.backend.ecommerce.orderline.OrderLineService;
import com.backend.ecommerce.payment.IPaymentClient;
import com.backend.ecommerce.payment.PaymentRequest;
import com.backend.ecommerce.product.ProductClient;
import com.backend.ecommerce.product.PurchaseRequest;
import com.backend.ecommerce.product.PurchaseResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final IOrderRepository repository;
    private final IOrderMapper mapper;
    private final ICustomerClient iCustomerClient;
    private final IPaymentClient iPaymentClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    @Transactional
    public Integer createOrder(OrderRequest request) {
        return Optional.of(request)
                .map(this::getCustomer)
                .map(customer -> processOrder(request, customer))
                .orElseThrow(() -> new BusinessException("Failed to create order"));
    }

    private CustomerResponse getCustomer(OrderRequest request) {
        return iCustomerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));
    }

    private Integer processOrder(OrderRequest request, CustomerResponse customer) {
        return Optional.of(request)
                .map(req -> getPurchaseResponses(req.products()))
                .map(purchasedProducts -> saveOrder(request, customer, purchasedProducts))
                .orElseThrow(() -> new BusinessException("Failed to process order"));
    }

    private List<PurchaseResponse> getPurchaseResponses(List<PurchaseRequest> products) {
        return productClient.purchaseProducts(products);
    }

    private Integer saveOrder(OrderRequest request, CustomerResponse customer, List<PurchaseResponse> purchasedProducts) {
        final var order = repository.save(mapper.toOrder(request));
        saveOrderLines(order, request.products());
        requestPayment(request, order, customer);
        produceOrderConfirmation(request, customer, purchasedProducts);
        return order.getId();
    }

    private void requestPayment(OrderRequest request, Order order, CustomerResponse customer) {
        final PaymentRequest paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        iPaymentClient.requestOrderPayment(paymentRequest);
    }

    private void saveOrderLines(Order order, List<PurchaseRequest> purchaseRequests) {
        purchaseRequests.forEach(purchaseRequest -> orderLineService.saveOrderLine(
                new OrderLineRequest(
                        null,
                        order.getId(),
                        purchaseRequest.productId(),
                        purchaseRequest.quantity()
                )
        ));
    }

    private void produceOrderConfirmation(OrderRequest request, CustomerResponse customer, List<PurchaseResponse> purchasedProducts) {
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );
    }

    public List<OrderResponse> findAllOrders() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .toList();
    }

    public OrderResponse findById(Integer id) {
        return repository.findById(id)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}