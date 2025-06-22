package com.charmflex.app.groceryapp.inventory_service.inventory.domain.models

import java.time.Instant

data class Inventory(
    val id: Long,
    val name: String,
    val category: String,
    val manufactureTime: Instant,
    val expiryTime: Instant
)