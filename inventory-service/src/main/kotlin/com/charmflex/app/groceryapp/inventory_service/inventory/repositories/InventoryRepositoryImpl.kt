package com.charmflex.app.groceryapp.inventory_service.inventory.repositories

import com.charmflex.app.groceryapp.inventory_service.exceptions.GenericException
import com.charmflex.app.groceryapp.inventory_service.exceptions.InventoryException
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.Inventory
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.InventorySummary
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.UserGrocery
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.UserInventory
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.repositories.InventoryRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class InventoryRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : InventoryRepository {
    private val namedJDBCTemplate: NamedParameterJdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplate)
    override fun addGrocery(userGrocery: UserGrocery) {
        val sql = """
        INSERT INTO groceries (item_name, created_by, category_id)
        VALUES (:itemName, :createdBy, :categoryId)
        ON CONFLICT (created_by, item_name) DO NOTHING
         """.trimIndent()

        val params = mapOf(
            "itemName" to userGrocery.name,
            "createdBy" to userGrocery.userId,
            "categoryId" to userGrocery.categoryId
        )

        try {
            namedJDBCTemplate.update(sql, params)
        } catch (exception: DuplicateKeyException) {
            throw InventoryException.DuplicatedGroceryException
        } catch (exception: Exception) {
            throw GenericException
        }
    }

    override fun removeGrocery(groceryId: Long) {
        val sql = "DELETE FROM groceries WHERE id = :id"
        val params = mapOf("id" to groceryId)


        try {
            namedJDBCTemplate.update(sql, params)
        } catch (ex: DataIntegrityViolationException) {
            // Likely caused by foreign key RESTRICT (i.e., still in use)
            throw InventoryException.GroceryRemovalRestriction
        } catch (exception: Exception) {
            throw GenericException
        }
    }

    override fun addInventory(userInventory: UserInventory) {
        val sql = """
            INSERT INTO grocery_inventories (grocery_id, quantity, expiry_date)
            VALUES (:id, :quantity, :expiryDate)
            ON CONFLICT (grocery_id, expiry_date)
            DO UPDATE SET quantity = grocery_inventories.quantity + EXCLUDED.quantity
        """.trimIndent()

        val params = mapOf(
            "id" to userInventory.groceryId,
            "quantity" to userInventory.quantity,
            "expiryDate" to userInventory.expiryDate
        )

        try {
            namedJDBCTemplate.update(sql, params)
        } catch (exception: Exception) {
            throw GenericException
        }
    }

    override fun removeInventory(inventoryId: Long, quantity: Int) {
        val selectSql = """
            SELECT quantity FROM grocery_inventories WHERE id = :id
        """.trimIndent()

        val params = mapOf("id" to inventoryId)

        val currentQuantity: Int? = try {
            namedJDBCTemplate.queryForObject(selectSql, params, Int::class.java)
        } catch (ex: EmptyResultDataAccessException) {
            throw InventoryException.ItemNotFound
        }

        if (currentQuantity == null) {
            throw InventoryException.ItemNotFound
        }


        if (currentQuantity < quantity) {
            throw InventoryException.InvalidOperation
        }

        val updateSql = """
        UPDATE grocery_inventories
            SET quantity = quantity - :deduct
            WHERE id = :id;
            
        DELETE FROM grocery_inventories WHERE id = :id AND quantity = 0;
    """.trimIndent()

        val updateParams = mapOf(
            "id" to inventoryId,
            "deduct" to quantity
        )

        namedJDBCTemplate.update(updateSql, updateParams)
    }

    override fun getUserInventory(userId: Int): InventorySummary {
        val sql = """ 
            SELECT 
                gi.id AS inventory_id,
                gi.quantity,
                gi.expiry_date,
                g.id AS grocery_id,
                g.item_name,
                c.id AS category_id,
                c.name AS category_name
            FROM grocery_inventories gi
            JOIN groceries g ON gi.grocery_id = g.id
            JOIN inventory_categories c ON g.category_id = c.id
            WHERE g.created_by = :userId
            ORDER BY gi.expiry_date DESC
        """

        val params = mapOf("userId" to userId)

        val results: List<InventorySummary.UserInventory> = namedJDBCTemplate.query(sql, params) { rs, _ ->
            InventorySummary.UserInventory(
                id = rs.getInt("inventory_id"),
                quantity = rs.getInt("quantity"),
                expiryDate = rs.getDate("expiry_date"),
                groceryDetail = InventorySummary.GroceryDetail(
                    id = rs.getLong("grocery_id"),
                    name = rs.getString("item_name"),
                    categoryDetail = InventorySummary.CategoryDetail(
                        id = rs.getInt("category_id"),
                        name = rs.getString("category_name")
                    )
                )
            )
        }

        return InventorySummary(inventories = results)
    }
}