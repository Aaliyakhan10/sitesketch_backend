package com.doftec.sitesketch.service

import com.doftec.sitesketch.model.Resume
import com.doftec.sitesketch.model.User
import com.doftec.sitesketch.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service

class DatabaseService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder,private val emailService: EmailService) {
    fun addUser(user: User): String {
        val token = UUID.randomUUID().toString()
        val user = User(
            email = user.email,
            password = passwordEncoder.encode(user.password),
            verificationToken = token,
            tokenExpiration = LocalDateTime.now().plusHours(24)
        )


        emailService.sendVerificationEmail(user.email, token)
        userRepository.save(user)
        return "User Register Successfully Now Login"
    }
    fun saveUser(user: User?):User{
      return userRepository.save<User>(user as User)
    }


        fun addResume(resume: Resume,email: String): User {
            val user =userRepository.findByEmail(email)
            user?.content=resume

            return userRepository.save(user as User)
        }
    fun addCode(code: String, email: String): User {
        val user =userRepository.findByEmail(email)
        user?.code=code

        return userRepository.save(user as User)
    }
    fun getCode(email: String): String{
        val user=userRepository.findByEmail(email)
        try{
          return  user?.code.toString()

        }catch (e: Exception){
          return  "${e.message}"
        }
    }
    fun getResume(email: String): Resume? {
        val user=userRepository.findByEmail(email)

            return  user?.content


    }
    fun saveUserInfo(resume: Resume,email: String): User {
        val user=userRepository.findByEmail(email);
        user?.content=resume
        return userRepository.save<User>(user as User)
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