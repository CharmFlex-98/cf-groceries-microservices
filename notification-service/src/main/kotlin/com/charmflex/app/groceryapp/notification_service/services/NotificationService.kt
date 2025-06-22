package com.charmflex.app.groceryapp.notification_service.services

import com.charmflex.app.groceryapp.events.InventoryCreatedEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service



@Service
class NotificationService {
    @KafkaListener(topics = ["inventory-created"])
    fun listen(inventoryCreatedEvent: InventoryCreatedEvent) {
        println("event received: $inventoryCreatedEvent")
    }
}