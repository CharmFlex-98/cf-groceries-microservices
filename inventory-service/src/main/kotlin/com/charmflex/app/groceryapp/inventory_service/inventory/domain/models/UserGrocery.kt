package com.charmflex.app.groceryapp.inventory_service.inventory.domain.models

data class UserGrocery(
    val userId: Int,
    val name: String,
    val categoryId: Int
)