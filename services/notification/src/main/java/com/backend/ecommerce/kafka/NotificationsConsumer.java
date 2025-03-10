package com.backend.ecommerce.kafka;

import com.backend.ecommerce.email.EmailService;
import com.backend.ecommerce.kafka.order.OrderConfirmation;
import com.backend.ecommerce.kafka.payment.PaymentConfirmation;
import com.backend.ecommerce.notification.Notification;
import com.backend.ecommerce.notification.INotificationRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static com.backend.ecommerce.notification.NotificationType.ORDER_CONFIRMATION;
import static com.backend.ecommerce.notification.NotificationType.PAYMENT_CONFIRMATION;
import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationsConsumer {

    private final INotificationRepository repository;
    private final EmailService emailService;

    //@SneakyThrows
    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotifications(@NonNull PaymentConfirmation paymentConfirmation) {
        log.info(format("Consuming the message from payment-topic Topic:: %s", paymentConfirmation));
        processPaymentConfirmation()
                .andThen(sendPaymentSuccessEmail())
                .accept(paymentConfirmation);
    }

    private Consumer<PaymentConfirmation> processPaymentConfirmation() {
        return confirmation -> repository.save(
                Notification.builder()
                        .type(PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(confirmation)
                        .build()
        );
    }

    private Consumer<PaymentConfirmation> sendPaymentSuccessEmail() {
        return confirmation -> {
            final String customerName = confirmation.customerFirstname() + " " + confirmation.customerLastname();
            try {
                emailService.sendPaymentSuccessEmail(
                        confirmation.customerEmail(),
                        customerName,
                        confirmation.amount(),
                        confirmation.orderReference()
                );
            } catch (MessagingException e) {
                log.info("Error sending email:: {}", e.getMessage());
            }
        };
    }

    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotifications(OrderConfirmation orderConfirmation) {
        log.info(format("Consuming the message from order-topic Topic:: %s", orderConfirmation));
        processOrderConfirmation()
                .andThen(sendOrderConfirmationEmail())
                .accept(orderConfirmation);
    }

    private Consumer<OrderConfirmation> processOrderConfirmation() {
        return confirmation -> repository.save(
                Notification.builder()
                        .type(ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(confirmation)
                        .build()
        );
    }

    private Consumer<OrderConfirmation> sendOrderConfirmationEmail() {
        return confirmation -> {
            final var customerName = confirmation.customer().firstname() + " " + confirmation.customer().lastname();
            try {
                emailService.sendOrderConfirmationEmail(
                        confirmation.customer().email(),
                        customerName,
                        confirmation.totalAmount(),
                        confirmation.orderReference(),
                        confirmation.products()
                );
            } catch (MessagingException e) {
                log.info("Error sending email:: {}", e.getMessage());
            }
        };
    }
}
