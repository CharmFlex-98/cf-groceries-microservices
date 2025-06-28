package com.charmflex.app.groceryapp.inventory_service.inventory.repositories

import com.charmflex.app.groceryapp.inventory_service.exceptions.GenericException
import com.charmflex.app.groceryapp.inventory_service.exceptions.InventoryException
import com.charmflex.app.groceryapp.inventory_service.inventory.domain.models.*
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
         """.trimIndent()

        val params = mapOf(
            "itemName" to userGrocery.name.lowercase(),
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

    override fun removeGrocery(userId: Int, groceryId: Long) {
        val sql = "DELETE FROM groceries WHERE id = :id AND created_by = :userId"
        val params = mapOf("id" to groceryId, "userId" to userId)

        try {
            val rowAffected = namedJDBCTemplate.update(sql, params)
            if (rowAffected == 0) throw InventoryException.ItemNotFound
        } catch (ex: DataIntegrityViolationException) {
            // Likely caused by foreign key RESTRICT (i.e., still in use)
            throw InventoryException.GroceryRemovalRestriction
        } catch (exception: Exception) {
            throw GenericException
        }
    }

    override fun getGroceriesByUser(userId: Int): List<GroceryDetail> {
        val sql = """
            SELECT 
                g.id as item_id, 
                g.item_name,
                c.id as category_id, 
                c.name as category_name
            FROM groceries g 
            JOIN inventory_categories c
            ON g.category_id = c.id
            WHERE created_by = :userId
        """.trimIndent()
        val param = mapOf(
            "userId" to userId
        )

        try {
            return namedJDBCTemplate.query(sql, param) { rs, _ ->
                GroceryDetail(
                    rs.getLong("item_id"),
                    rs.getString("item_name"),
                    CategoryDetail(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                    )
                )
            }
        } catch (e: Exception) {
            throw GenericException
        }
    }

    override fun addInventory(addByUserId: Int, userInventory: UserInventory) {
        assertGroceryBelongsToUser(addByUserId, userInventory.groceryId)
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

    private fun assertGroceryBelongsToUser(userId: Int, groceryId: Long) {
        val sql = "SELECT COUNT(*) FROM groceries WHERE id = :id AND created_by = :userId"
        val params = mapOf("id" to groceryId, "userId" to userId)

        val count = namedJDBCTemplate.queryForObject(sql, params, Int::class.java)
        if (count == null || count == 0) {
            throw InventoryException.InvalidOperation
        }
    }


    override fun removeInventory(removeByUserId: Int, inventoryId: Long, quantity: Int) {
        val selectSql = """
            SELECT gi.quantity FROM grocery_inventories gi
            JOIN groceries g ON gi.grocery_id = g.id
            WHERE gi.id = :id AND g.created_by = :removeByUserId
        """.trimIndent()

        val params = mapOf(
            "id" to inventoryId,
            "removeByUserId" to removeByUserId
        )

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

    override fun getUserInventory(userId: Int): List<InventorySummary.UserInventory> {
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
                groceryDetail = GroceryDetail(
                    id = rs.getLong("grocery_id"),
                    name = rs.getString("item_name"),
                    categoryDetail = CategoryDetail(
                        id = rs.getInt("category_id"),
                        name = rs.getString("category_name")
                    )
                )
            )
        }

        return results
    }

    override fun getInventoryCategories(): List<GroceryCategory> {
        val sql = """
            SELECT * FROM inventory_categories
        """.trimIndent()

        return namedJDBCTemplate.query(sql) { result, _ ->
            GroceryCategory(
                id = result.getInt("id"),
                name = result.getString("name")
            )
        }
    }
}