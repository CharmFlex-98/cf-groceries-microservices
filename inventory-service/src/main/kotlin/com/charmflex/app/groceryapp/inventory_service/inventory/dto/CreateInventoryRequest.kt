package com.charmflex.app.groceryapp.inventory_service.inventory.dto

import java.time.Instant
import java.util.Date

data class CreateInventoryRequest(
    val quantity: Int,
    val expiryDate: Date?
)