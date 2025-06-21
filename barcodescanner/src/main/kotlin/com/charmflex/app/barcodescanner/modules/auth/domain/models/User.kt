package com.charmflex.app.barcodescanner.modules.auth.domain.models

import java.time.Instant

data class User(
    val id: Int = 0,
    val username: String,
    val hashedPassword: String,
    val createdAt: Instant,
    val modifiedAt: Instant,
    val lastLogin: Instant? = null
)