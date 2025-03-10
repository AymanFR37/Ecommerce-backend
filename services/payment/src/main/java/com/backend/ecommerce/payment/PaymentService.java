package com.backend.ecommerce.payment;

import com.backend.ecommerce.notification.NotificationProducer;
import com.backend.ecommerce.notification.PaymentNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IPaymentRepository repository;
    private final IPaymentMapper mapper;
    private final NotificationProducer notificationProducer;

    public Integer createPayment(PaymentRequest request) {
        return Optional.of(request)
                .map(mapper::toPayment)
                .map(payment -> {
                    repository.save(payment);
                    produceNotification(request);
                    return payment.getId();
                })
                .orElse(null);
    }

    private void produceNotification(PaymentRequest request) {
        this.notificationProducer.sendNotification(
                new PaymentNotificationRequest(
                        request.orderReference(),
                        request.amount(),
                        request.paymentMethod(),
                        request.customer().firstname(),
                        request.customer().lastname(),
                        request.customer().email()
                )
        );
    }
}
