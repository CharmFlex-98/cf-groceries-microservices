package com.charmflex.app.groceryapp.events

data class InventoryCreatedEvent(
    val id: Long,
    val name: String
)