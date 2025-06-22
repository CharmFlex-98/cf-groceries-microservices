package com.charmflex.app.barcodescanner.auth.models

data class RegisterUserRequest(
    val username: String,
    val password: String
)

data class GetUserIdRequest(
    val username: String
)