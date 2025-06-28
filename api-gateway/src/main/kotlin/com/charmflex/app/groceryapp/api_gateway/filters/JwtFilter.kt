package com.charmflex.app.groceryapp.api_gateway.filters

import com.charmflex.app.barcodescanner.auth.services.TokenService
import com.charmflex.app.groceryapp.api_gateway.constants.HeaderConstant
import com.charmflex.app.groceryapp.api_gateway.exceptions.ExceptionBase
import com.charmflex.app.groceryapp.api_gateway.exceptions.GatewayException
import com.charmflex.app.groceryapp.api_gateway.proxies.AuthProxy
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
internal class JwtFilter(
    private val authProxy: AuthProxy,
    @Value("\${gateway-auth-secret}")
    private val gatewayAuthSecret: String
) : OncePerRequestFilter() {
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
            val username = tokenService.extractUsername(token)
            if (username == null) {
                filterChain.doFilter(request, response)
                return
            }

            try {
                val userId = authProxy.getUserId(username).id
                val authentication = UsernamePasswordAuthenticationToken(username, null, null)
                SecurityContextHolder.getContext().authentication = authentication
                val modifiedRequest = modifiedRequest(request, userId.toString())

                filterChain.doFilter(modifiedRequest, response)
            } catch (e: Exception) {
                sendError(response, GatewayException.InvalidToken)
                return
            }
        } catch (e: ExpiredJwtException) {
            sendError(response, GatewayException.TokenExpired)
        } catch (e: Exception) {
            sendError(response, GatewayException.InvalidToken)
        }

    }

    private fun sendError(response: HttpServletResponse, authException: ExceptionBase) {
        response.status = authException.statusCode
        response.contentType = "application/json"
        response.writer.write(authException.toBodyString())
    }

    // Wrap the request and inject headers
    private fun modifiedRequest(request: HttpServletRequest, userId: String): HttpServletRequestWrapper {
        val wrappedRequest = object : HttpServletRequestWrapper(request) {
            override fun getHeader(name: String?): String? {
                return when (name) {
                    HeaderConstant.HEADER_USERID -> userId
                    HeaderConstant.HEADER_GATEWAY_SECRET -> gatewayAuthSecret
                    else -> super.getHeader(name)
                }
            }

            override fun getHeaderNames(): Enumeration<String> {
                val names = super.getHeaderNames().toList().toMutableSet()
                names.addAll(setOf(HeaderConstant.HEADER_USERID, HeaderConstant.HEADER_GATEWAY_SECRET))
                return Collections.enumeration(names)
            }
        }

        return wrappedRequest
    }


}