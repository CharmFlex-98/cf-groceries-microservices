package com.charmflex.app.barcodescanner.modules.auth.domain.repositories

import com.charmflex.app.barcodescanner.modules.auth.domain.models.User


internal interface UserRepository {
    fun saveUser(user: User)
    fun getUserByName(name: String): User?
    fun getUserById(id: Long)
}