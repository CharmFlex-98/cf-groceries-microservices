package com.charmflex.app.groceryapp.inventory_service.inventory.domain.repositories

import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.*

interface InventoryRepository {
    fun addGrocery(userGrocery: UserGrocery)
    fun removeGrocery(userId: Int, groceryId: Long)
    fun getGroceriesByUser(userId: Int): List<GroceryDetail>
    fun addInventory(addByUserId: Int, userInventory: UserInventory)
    fun removeInventory(removeByUserId: Int, inventoryId: Long, quantity: Int)
    fun getUserInventory(userId: Int): List<InventorySummary.UserInventory>
    fun getInventoryCategories(): List<GroceryCategory>
}