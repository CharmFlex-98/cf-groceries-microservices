package com.charmflex.app.barcodescanner.exceptions

data class ErrorResponseBody(
    val errorCode: String,
    val errorMessage: String
)