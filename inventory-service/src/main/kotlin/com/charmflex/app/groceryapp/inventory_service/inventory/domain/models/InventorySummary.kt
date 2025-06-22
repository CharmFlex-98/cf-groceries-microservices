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

    data class GroceryDetail(
        val id: Long,
        val name: String,
        val categoryDetail: CategoryDetail
    )

    data class CategoryDetail(
        val id: Int,
        val name: String
    )
}