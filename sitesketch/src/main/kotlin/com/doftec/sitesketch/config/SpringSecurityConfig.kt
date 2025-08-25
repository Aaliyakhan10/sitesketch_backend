package com.doftec.sitesketch.config

import com.doftec.sitesketch.Utils.JwtUtil
import com.doftec.sitesketch.filter.JwtFilter
import com.doftec.sitesketch.service.UserDetailsServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder


@Configuration
@EnableWebSecurity
class SpringSecurityConfig(
    private val userDetailsService: UserDetailsServiceImpl,
    private val jwtFilter: JwtFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()


    @Bean
    fun authenticationManager(
        http: HttpSecurity
    ): AuthenticationManager {
        val authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authManagerBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
        return authManagerBuilder.build()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/register", "/login","/validate" , "/check-verification" ,"status").permitAll()
                it.requestMatchers("/admin/**").hasRole("ADMIN")
                it.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                it.anyRequest().permitAll()
            }
            .userDetailsService(userDetailsService)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .formLogin { it.disable() }
            .httpBasic { }

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

}
