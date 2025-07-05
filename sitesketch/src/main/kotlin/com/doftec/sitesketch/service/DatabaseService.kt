package com.doftec.sitesketch.service

import com.doftec.sitesketch.model.Resume
import com.doftec.sitesketch.model.User
import com.doftec.sitesketch.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service

class DatabaseService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder,) {
    fun addUser(user: User): User {
        val hashedPassword = passwordEncoder.encode(user.password)
        user.password=hashedPassword
        return userRepository.save(user)
    }


        fun addResume(resume: Resume,email: String): User {
            val user =userRepository.findByEmail(email)
            user?.content=resume

            return userRepository.save(user as User)
        }
        fun getCurrentUserEmail(): String? {
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication != null && authentication.isAuthenticated) {
                val principal = authentication.principal
                return when (principal) {
                    is UserDetails -> principal.username  // here username is email
                    is String -> principal  // sometimes it can be just email as String
                    else -> null
                }
            }
            return null
        }




    }