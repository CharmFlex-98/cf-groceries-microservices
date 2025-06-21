package com.charmflex.app.barcodescanner.modules.auth.repositories

import com.charmflex.app.barcodescanner.exceptions.AuthException
import com.charmflex.app.barcodescanner.exceptions.GenericException
import com.charmflex.app.barcodescanner.modules.auth.domain.models.User
import com.charmflex.app.barcodescanner.modules.auth.domain.repositories.UserRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
internal class UserRepositoryImpl(
    jdbcTemplate: JdbcTemplate
) : UserRepository {
    private val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplate)
    override fun saveUser(user: User) {
        val username = user.username
        val password = user.hashedPassword
        val createdAt = user.createdAt
        val modifiedAt = user.modifiedAt
        val lastLogin = user.lastLogin;

        val sql = """
            INSERT INTO users (username, hashed_password, last_login, created_at, modified_at) VALUES (:username, :password, :lastLogin, :createdAt, :modifiedAt)
        """.trimIndent()
        val params = mapOf(
            "username" to username,
            "password" to password,
            "createdAt" to Timestamp.from(createdAt),
            "modifiedAt" to Timestamp.from(modifiedAt),
            "lastLogin" to Timestamp.from(modifiedAt)
        )

        try {
            namedParameterJdbcTemplate.update(sql, params)
        } catch (e: DuplicateKeyException) {
            throw AuthException.UserExisted
        } catch (e: Exception) {
            println(e.message)
            throw AuthException.RegisterError
        }
    }


    override fun getUserByName(name: String): User? {
        val sql = """
            SELECT * FROM users
            WHERE username = :name
        """.trimIndent()
        val params = mapOf(
            "name" to name
        )

        return try {
            namedParameterJdbcTemplate.queryForObject(sql, params) { res, _ ->
                User(
                    id = res.getInt("id"),
                    username = res.getString("username"),
                    hashedPassword = res.getString("hashed_password"),
                    lastLogin = res.getTimestamp("last_login").toInstant(),
                    createdAt = res.getTimestamp("created_at").toInstant(),
                    modifiedAt = res.getTimestamp("modified_at").toInstant()
                )
            }
        } catch (e: EmptyResultDataAccessException) {
            return null
        } catch (e: Exception) {
            throw GenericException
        }
    }

    override fun getUserById(id: Long) {
        TODO("Not yet implemented")
    }
}