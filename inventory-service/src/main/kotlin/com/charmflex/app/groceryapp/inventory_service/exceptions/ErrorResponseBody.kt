package com.charmflex.app.groceryapp.inventory_service.exceptions

data class ErrorResponseBody(
    val errorCode: String,
    val errorMessage: String
)