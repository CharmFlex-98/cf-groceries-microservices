package com.charmflex.app.groceryapp.inventory_service.inventory.domain.models

import java.util.*

data class UserInventory(
    val id: Long = 0,
    val groceryId: Long,
    val quantity: Int,
    val expiryDate: Date?
)