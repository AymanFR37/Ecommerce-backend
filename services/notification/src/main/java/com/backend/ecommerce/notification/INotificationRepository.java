package com.backend.ecommerce.notification;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface INotificationRepository extends MongoRepository<Notification, String> {
}
