package com.charmflex.app.groceryapp.inventory_service.inventory.controller
import com.charmflex.app.groceryapp.events.InventoryCreatedEvent
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.*
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.repositories.InventoryRepository
import com.charmflex.app.groceryapp.inventory_service.inventory.dto.CreateGroceryRequest
import com.charmflex.app.groceryapp.inventory_service.inventory.dto.CreateInventoryRequest
import com.charmflex.app.groceryapp.inventory_service.inventory.dto.RemoveInventoryRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("api/v1/inventory")
class InventoryController(
    private val repository: InventoryRepository,
    private val kafkaTemplate: KafkaTemplate<String, InventoryCreatedEvent>,
    @Value("\${gateway-auth-secret}")
    private val gatewaySecret: String
) {
    private val HEADER_USERID = "X-Gateway-UserId"
    private val HEADER_GATEWAY_SECRET = "X-Gateway-Secret"


    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/groceries/create")
    fun addGroceryByUser(
        @RequestHeader("X-Gateway-UserId")
        userId: String,
        @RequestBody
        createGroceryRequest: CreateGroceryRequest
    ) {
        repository.addGrocery(
            UserGrocery(
                userId = userId.toInt(),
                name = createGroceryRequest.name,
                categoryId = createGroceryRequest.categoryId
            )
        )
        kafkaTemplate.send("inventory-created", InventoryCreatedEvent(2, createGroceryRequest.name))
    }

    @ResponseStatus(code = HttpStatus.OK)
    @DeleteMapping("/groceries/{groceryId}/delete")
    fun removeGroceryByUser(
        @RequestHeader("X-Gateway-UserId")
        userId: String,
        @PathVariable
        groceryId: Long
    ) {
        repository.removeGrocery(userId.toInt(), groceryId)
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/groceries/all")
    fun getGroceriesByUser(
        @RequestHeader("X-Gateway-UserId")
        userId: String,
    ): List<GroceryDetail> {
        return repository.getGroceriesByUser(userId = userId.toInt())
    }

    @ResponseStatus(code = HttpStatus.OK)
    @PutMapping("/groceries/{groceryId}/add")
    fun addInventory(
        @RequestHeader("X-Gateway-UserId")
        userId: String,
        @PathVariable
        groceryId: Long,
        @RequestBody
        createInventoryRequest: CreateInventoryRequest
    ) {
        repository.addInventory(
            userId.toInt(),
            UserInventory(
                groceryId = groceryId,
                quantity = createInventoryRequest.quantity,
                expiryDate = createInventoryRequest.expiryDate
            )
        )

    }

    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping("/inventory/{inventoryId}/remove")
    fun removeInventory(
        @RequestHeader("X-Gateway-UserId")
        userId: String,
        @PathVariable
        inventoryId: Long,
        @RequestBody
        removeInventoryRequest: RemoveInventoryRequest
    ) {
        repository.removeInventory(
            userId.toInt(),
            inventoryId = inventoryId,
            quantity = removeInventoryRequest.quantity
        )
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/inventory/all")
    fun getUserInventory(
        @RequestHeader("X-Gateway-UserId")
        userId: String,
    ): List<InventorySummary.UserInventory> {
        return repository.getUserInventory(userId.toInt())
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/category/all")
    fun getAllInventoryCategories(): List<GroceryCategory> {
        return repository.getInventoryCategories()
    }
}