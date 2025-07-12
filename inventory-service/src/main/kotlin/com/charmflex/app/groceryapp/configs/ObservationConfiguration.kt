package com.charmflex.app.groceryapp.configs

import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.aop.ObservedAspect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ObservationConfiguration {
    @Bean
    fun observedAspect(registry: ObservationRegistry?): ObservedAspect {
        return ObservedAspect(registry!!)
    }
}