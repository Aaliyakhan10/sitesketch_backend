package com.doftec.sitesketch.config

import com.doftec.sitesketch.service.UserDetailsServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SpringSecurityConfig(
    private val userDetailsServiceImpl: UserDetailsServiceImpl,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager =
        authConfig.authenticationManager

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/register", "/login").permitAll()
                it.requestMatchers("/admin/**").hasRole("ADMIN")
                it.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                it.anyRequest().permitAll()
            }
            .formLogin { it.disable() }
            .httpBasic { }
            .build()
}
