package com.charmflex.app.barcodescanner.auth.domain.repositories

import com.charmflex.app.barcodescanner.auth.domain.models.User


internal interface UserRepository {
    fun saveUser(user: User)
    fun getUserByName(name: String): User?
    fun getUserById(id: Long)
    fun isLegitUser(userId: Int): Boolean
}