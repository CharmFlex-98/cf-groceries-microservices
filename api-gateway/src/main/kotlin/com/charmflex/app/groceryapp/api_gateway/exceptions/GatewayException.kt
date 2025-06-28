package com.charmflex.app.groceryapp.api_gateway.exceptions

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

sealed interface GatewayException {
    object TokenExpired : ExceptionBase(HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_EXPIRED", "expired")
    object InvalidToken : ExceptionBase(HttpServletResponse.SC_BAD_REQUEST, "INVALID_TOKEN", "invalid")
}