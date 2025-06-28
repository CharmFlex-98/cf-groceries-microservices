package com.charmflex.app.groceryapp.api_gateway.routes

import com.charmflex.app.groceryapp.api_gateway.filters.GatewayHeadersFilterFunction
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.RequestPredicate
import org.springframework.web.servlet.function.RequestPredicates
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import java.net.URI

@Configuration
class Routes(
    @Value("\${identity-service-url}")
    private val authServiceUrl: String,
    @Value("\${inventory-service-url}")
    private val inventoryServiceUrl: String
) {
    private val FALLBACK_ROUTE = "/fallbackRoute"

    @Bean
    fun inventoryRoute(): RouterFunction<ServerResponse> {
        return GatewayRouterFunctions
            .route("inventory-service")
            .route(RequestPredicates.path("/api/v1/inventory/**"), HandlerFunctions.http(inventoryServiceUrl))
            .filter(GatewayHeadersFilterFunction())
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker", URI.create("forward:$FALLBACK_ROUTE")))
            .build()
    }

    @Bean
    fun authService(): RouterFunction<ServerResponse> {
        return GatewayRouterFunctions
            .route("auth-service")
            .route(RequestPredicates.path("/api/v1/auth/**"), HandlerFunctions.http(authServiceUrl))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("authServiceCircuitBreaker", URI.create("forward:$FALLBACK_ROUTE")))
            .build()
    }

    @Bean
    fun fallbackRoute(): RouterFunction<ServerResponse> {
        return GatewayRouterFunctions
            .route("fallbackRoute")
            .PUT(FALLBACK_ROUTE) {
                ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service not available. Please try again later.")
            }
            .build()
    }
}