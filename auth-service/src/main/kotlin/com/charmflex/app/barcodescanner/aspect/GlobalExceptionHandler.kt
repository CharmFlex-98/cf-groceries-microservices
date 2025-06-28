package com.charmflex.app.barcodescanner.aspect

import com.charmflex.app.barcodescanner.exceptions.ExceptionBase
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
internal class GlobalExceptionHandler {
    @ExceptionHandler(ExceptionBase::class)
    fun handleException(exceptionBase: ExceptionBase): ResponseEntity<Map<String, Any>> {
        val body = mapOf(
            "errorCode" to exceptionBase.errorCode,
            "errorMessage" to exceptionBase.errorMessage
        )
        return ResponseEntity
            .status(exceptionBase.statusCode)
            .body(body)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleSecurityException(exception: BadCredentialsException): ResponseEntity<Map<String, Any>> {
        val body = mapOf(
            "errorCode" to "5555",
            "errorMessage" to "Cannot"
        )
        return ResponseEntity
            .status(HttpServletResponse.SC_BAD_GATEWAY)
            .body(body)
    }
}