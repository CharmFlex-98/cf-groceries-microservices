package com.charmflex.app.groceryapp.api_gateway.proxies

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

data class UserIdResponse(
    val id: Int
)

@FeignClient(
    name = "auth",
    url = "\${identity-service-url}/api/v1/auth"
)
interface AuthProxy {
    @GetMapping("/users/{username}/legit")
    fun isLegitUser(@PathVariable username: String): Boolean

    @GetMapping("/users/{username}")
    fun getUserId(@PathVariable username: String): UserIdResponse
}