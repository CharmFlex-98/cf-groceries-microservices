package com.charmflex.app.groceryapp.api_gateway.configuration


import com.charmflex.app.groceryapp.api_gateway.filters.JwtFilter
import com.charmflex.app.groceryapp.api_gateway.proxies.AuthProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.AuthorizationFilter
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.authentication.AuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutFilter


@Configuration
//@EnableWebSecurity
internal class SecurityConfiguration {
    private val BASE_API = "api/v1/auth"

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtFilter: JwtFilter): SecurityFilterChain {
        return http
            .csrf {
                it.disable()
            }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "$BASE_API/login",
                        "$BASE_API/register",
                        "/privacy-policy.html",
                        "/fallbackRoute",
                        "/actuator/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { }
            .addFilterBefore(jwtFilter, AuthorizationFilter::class.java)
            .build()
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        return AuthenticationManager { throw Exception("No authentication manager is needed") }
    }
}