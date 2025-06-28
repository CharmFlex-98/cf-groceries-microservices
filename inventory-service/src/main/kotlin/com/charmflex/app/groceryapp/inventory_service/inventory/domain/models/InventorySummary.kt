package com.charmflex.app.groceryapp.inventory_service.inventory.domain.models

import java.util.Date

data class InventorySummary(
    val inventories: List<UserInventory>
) {
    data class UserInventory(
        val id: Int,
        val groceryDetail: GroceryDetail,
        val quantity: Int,
        val expiryDate: Date,
    )
}