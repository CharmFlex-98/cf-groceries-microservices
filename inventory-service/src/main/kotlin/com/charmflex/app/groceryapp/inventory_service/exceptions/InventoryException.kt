package com.charmflex.app.groceryapp.inventory_service.exceptions

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse

abstract class ExceptionBase(
    val statusCode: Int,
    val errorCode: String,
    val errorMessage: String
) : Throwable() {
    fun toBodyString(): String {
        val objectMapper = ObjectMapper()
        return objectMapper.writer().writeValueAsString(mapOf("errorCode" to errorCode, "errorMessage" to errorCode))
    }
}

object GenericException : ExceptionBase(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "GENERIC_ERROR", "generic error occurred.")

sealed interface InventoryException {
    object DuplicatedGroceryException : ExceptionBase(HttpServletResponse.SC_CONFLICT, "DUPLICATED_GROCERY", "the user already has the grocery saved previously")
    object GroceryRemovalRestriction : ExceptionBase(HttpServletResponse.SC_BAD_REQUEST, "REMOVAL_FORBIDDEN", "the user already has the grocery saved previously")
    object ItemNotFound : ExceptionBase(HttpServletResponse.SC_BAD_REQUEST, "ITEM_NOT_FOUND", "invalid operation on non-existing object")
    object InvalidOperation : ExceptionBase(HttpServletResponse.SC_BAD_REQUEST, "INVALID_OPERATION", "invalid operation. Check the validity on the request payload")
}

