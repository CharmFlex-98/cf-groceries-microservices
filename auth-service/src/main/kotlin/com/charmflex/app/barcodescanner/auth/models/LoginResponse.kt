package com.charmflex.app.barcodescanner.auth.models

internal data class LoginResponse(
    val success: Boolean,
    val token: String
)

data class UserIdResponse(
    val id: Int
)