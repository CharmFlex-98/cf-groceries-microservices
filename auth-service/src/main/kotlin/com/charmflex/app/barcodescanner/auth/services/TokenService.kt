package com.charmflex.app.barcodescanner.auth.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey


@Service
class TokenService {
    private val secret = "thisIsMysecregtfrdesww233eggtffeeddgkjjhhtdhttebd54ndhdhfhhhshs8877465sbbdd"
    private val signingKey: SecretKey
        get() {
            val keyBytes: ByteArray = Base64.getDecoder().decode(secret)
            return Keys.hmacShaKeyFor(keyBytes)
        }

    fun generateToken(subject: String, additionalClaims: Map<String, Any> = emptyMap()): String {
        val currentTime = System.currentTimeMillis()
        return Jwts.builder()
            .claims(additionalClaims)
            .subject(subject)
            .issuer("CharmFlex Studio")
            .issuedAt(Date(currentTime))
            .expiration(Date(currentTime +  5 * 60 * 1000))
            .signWith(signingKey)
            .compact()
    }

    fun validateAndExtractUsername(token: String): String? {
        return extractAllClaims(token).subject
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}