package com.doftec.sitesketch.service

import com.doftec.sitesketch.repository.UserRepository
import org.springframework.security.authentication.DisabledException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found")
        println("User found: $user")
        println("Password from DB: ${user.password}")
        if (!user.enabled) {
            throw DisabledException("Email not verified")
        }

        val authorities = user.roles.map {
            SimpleGrantedAuthority("ROLE_$it")
        }

        return org.springframework.security.core.userdetails.User(
            user.email,
            user.password,
            authorities
        )
    }
}
