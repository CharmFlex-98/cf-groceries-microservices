package com.charmflex.app.groceryapp.inventory_service.inventory.domain.models

data class GroceryDetail(
    val id: Long,
    val name: String,
    val categoryDetail: CategoryDetail
)