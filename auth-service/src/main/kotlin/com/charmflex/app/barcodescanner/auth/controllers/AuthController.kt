package com.charmflex.app.barcodescanner.auth.controllers

import com.charmflex.app.barcodescanner.exceptions.AuthException
import com.charmflex.app.barcodescanner.auth.domain.models.User
import com.charmflex.app.barcodescanner.auth.domain.repositories.UserRepository
import com.charmflex.app.barcodescanner.auth.models.LoginRequest
import com.charmflex.app.barcodescanner.auth.models.LoginResponse
import com.charmflex.app.barcodescanner.auth.models.RegisterUserRequest
import com.charmflex.app.barcodescanner.auth.models.UserIdResponse
import com.charmflex.app.barcodescanner.auth.services.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("api/v1/auth")
internal class AuthController(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val tokenService: TokenService,
    private val passwordEncoder: PasswordEncoder
) {
    @PostMapping("/login")
    fun loginUser(@RequestBody loginUserRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val auth = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(loginUserRequest.username, loginUserRequest.password))
        return ResponseEntity.ok(LoginResponse(true, tokenService.generateToken(auth.name)))
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody registerUserRequest: RegisterUserRequest): ResponseEntity<Unit> {
        val newUser = User(
            id = 0,
            registerUserRequest.username,
            passwordEncoder.encode(registerUserRequest.password),
            createdAt = Instant.now(),
            modifiedAt = Instant.now(),
        )
        userRepository.saveUser(newUser)

        return ResponseEntity.ok(Unit)
    }

    @GetMapping("/users/{username}")
    @ResponseStatus(HttpStatus.OK)
    fun getUserId(@PathVariable username: String): UserIdResponse {
        return userRepository.getUserByName(username)?.let { UserIdResponse(it.id) } ?: throw AuthException.UserNotFound
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/users/{username}/legit")
    fun isUserLegit(@PathVariable username: String): Boolean {
        return userRepository.getUserByName(username)?.let { true } ?: false
    }
}