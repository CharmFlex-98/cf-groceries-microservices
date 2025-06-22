package com.charmflex.app.groceryapp.inventory_service.inventory.controller
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.InventorySummary
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.UserGrocery
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.UserInventory
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.repositories.InventoryRepository
import com.charmflex.app.groceryapp.inventory_service.inventory.dto.CreateGroceryRequest
import com.charmflex.app.groceryapp.inventory_service.inventory.dto.CreateInventoryRequest
import com.charmflex.app.groceryapp.inventory_service.inventory.dto.RemoveInventoryRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/inventory")
class InventoryController(
    private val repository: InventoryRepository
) {
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/groceries/create")
    fun addGroceryByUser(@RequestBody createGroceryRequest: CreateGroceryRequest) {
        repository.addGrocery(
            UserGrocery(
                2,
                name = createGroceryRequest.name,
                categoryId = createGroceryRequest.categoryId
            )
        )
    }

    @ResponseStatus(code = HttpStatus.OK)
    @DeleteMapping("/groceries/{groceryId}/delete")
    fun removeGroceryByUser(@PathVariable groceryId: Long) {
        repository.removeGrocery(groceryId)
    }

    @ResponseStatus(code = HttpStatus.OK)
    @PutMapping("/groceries/{groceryId}/add")
    fun addInventory(
        @PathVariable groceryId: Long,
        @RequestBody createInventoryRequest: CreateInventoryRequest
    ) {
        repository.addInventory(
            UserInventory(
                groceryId = groceryId,
                quantity = createInventoryRequest.quantity,
                expiryDate = createInventoryRequest.expiryDate
            )
        )
    }

    @ResponseStatus(code = HttpStatus.OK)
    @DeleteMapping("/inventory/{inventoryId}/remove")
    fun removeInventory(
        @PathVariable inventoryId: Long,
        @RequestBody removeInventoryRequest: RemoveInventoryRequest
    ) {
        repository.removeInventory(
            inventoryId = inventoryId,
            quantity = removeInventoryRequest.quantity
        )
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/inventory/all")
    fun getUserInventory(): InventorySummary {
        return repository.getUserInventory(2)
    }
}