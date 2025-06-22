package com.charmflex.app.groceryapp.inventory_service.inventory.domain.repositories

import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.Inventory
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.InventorySummary
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.UserGrocery
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.UserInventory

interface InventoryRepository {
    fun addGrocery(userGrocery: UserGrocery)
    fun removeGrocery(groceryId: Long)
    fun addInventory(userInventory: UserInventory)
    fun removeInventory(inventoryId: Long, quantity: Int)
    fun getUserInventory(userId: Int): InventorySummary
}