package com.charmflex.app.groceryapp.api_gateway.configuration

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients(
    basePackages = ["com.charmflex.app.groceryapp.api_gateway.proxies"]
)
class MainConfiguration {
}