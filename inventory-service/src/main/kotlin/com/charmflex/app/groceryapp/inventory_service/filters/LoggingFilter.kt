package com.charmflex.app.groceryapp.inventory_service.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class LoggingFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val wrappedRequest = ContentCachingRequestWrapper(request)
        val wrappedResponse = ContentCachingResponseWrapper(response)

        filterChain.doFilter(wrappedRequest, wrappedResponse)

        val requestBody = String(wrappedRequest.contentAsByteArray)
        val responseBody = String(wrappedResponse.contentAsByteArray)

        logger.info("Request URI: ${request.requestURI}")
        logger.info("Request Body: $requestBody")
        logger.info("Response Body: $responseBody")

        wrappedResponse.copyBodyToResponse()
    }
}
