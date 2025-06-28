package com.charmflex.app.groceryapp.inventory_service.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class GatewayFilter(
    @Value("\${gateway-auth-secret}")
    private val gatewaySecret: String
) : OncePerRequestFilter() {
    private val HEADER_GATEWAY_SECRET = "X-Gateway-Secret"

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val headerSecret = request.getHeader(HEADER_GATEWAY_SECRET)

        if (headerSecret.isNullOrBlank() || headerSecret != gatewaySecret) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Invalid gateway secret")
            return
        }

        filterChain.doFilter(request, response)
    }
}