package com.charmflex.app.groceryapp.api_gateway.configuration

import TraceIDResponseFilter
import io.micrometer.tracing.Tracer
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients(
    basePackages = ["com.charmflex.app.groceryapp.api_gateway.proxies"]
)
class MainConfiguration {

    @Bean
    fun traceIdResponseFilter(tracer: Tracer): FilterRegistrationBean<TraceIDResponseFilter> {
        val registration = FilterRegistrationBean(TraceIDResponseFilter(tracer))
        registration.order = -100  // Very early, before Spring Security filters
        return registration
    }
}