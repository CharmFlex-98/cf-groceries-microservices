package com.charmflex.app.barcodescanner.auth.services

import com.charmflex.app.barcodescanner.exceptions.AuthException
import com.charmflex.app.barcodescanner.auth.domain.repositories.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
internal class UserDetailServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw AuthException.UserNotFound
        }
        userRepository.getUserByName(username)?.let {
            return User(
                it.username,
                it.hashedPassword,
                emptyList()
            )
        } ?: throw AuthException.UserNotFound
    }
}