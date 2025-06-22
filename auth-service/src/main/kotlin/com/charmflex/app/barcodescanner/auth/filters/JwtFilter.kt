package com.charmflex.app.barcodescanner.auth.filters
import com.charmflex.app.barcodescanner.exceptions.AuthException
import com.charmflex.app.barcodescanner.exceptions.ExceptionBase
import com.charmflex.app.barcodescanner.exceptions.GenericException
import com.charmflex.app.barcodescanner.auth.services.TokenService
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.json.JsonWriter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

internal class JwtFilter : OncePerRequestFilter() {
    private val AUTH_HEADER_KEY = "Authorization"
    private val AUTH_HEADER_VALUE_PREFIX = "Bearer "
    private val tokenService = TokenService()
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeaderContent = request.getHeader(AUTH_HEADER_KEY)
        if (authHeaderContent == null || !authHeaderContent.startsWith(AUTH_HEADER_VALUE_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeaderContent.substringAfter(AUTH_HEADER_VALUE_PREFIX)
        try {
            val username = tokenService.validateAndExtractUsername(token)
            if (username == null) {
                filterChain.doFilter(request, response)
                return
            }

            val authentication = UsernamePasswordAuthenticationToken(username, null, null)
            SecurityContextHolder.getContext().authentication = authentication

            filterChain.doFilter(request, response)
        } catch (e: ExpiredJwtException) {
            sendError(response, AuthException.TokenExpired)
        } catch (e: Exception) {
            sendError(response, AuthException.InvalidToken)
        }

    }

    private fun sendError(response: HttpServletResponse, authException: ExceptionBase) {
        response.status = authException.statusCode
        response.contentType = "application/json"
        response.writer.write(authException.toBodyString())
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val matcher = AntPathMatcher()
        return matcher.match("api/v1/auth/register", request.servletPath)
    }
}