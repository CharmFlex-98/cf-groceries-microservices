package com.charmflex.app.barcodescanner.exceptions

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import java.rmi.ServerException

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

sealed interface AuthException {
    object InvalidToken : ExceptionBase(HttpServletResponse.SC_BAD_REQUEST, "INVALID_TOKEN", "invalid")
    object TokenExpired : ExceptionBase(HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_EXPIRED", "expired")
    object MissingToken : ExceptionBase(HttpServletResponse.SC_UNAUTHORIZED, "MISSING_TOKEN", "Not authenticated")
    object UserNotFound : ExceptionBase(HttpServletResponse.SC_BAD_REQUEST, "USER_NOT_FOUND", "cannot find user")
    object RegisterError : ExceptionBase(HttpServletResponse.SC_BAD_REQUEST, "REGISTRATION_ERROR", "Unknown error")
    object UserExisted : ExceptionBase(HttpServletResponse.SC_CONFLICT, "USER_EXIST", "user with same name is found")
}

