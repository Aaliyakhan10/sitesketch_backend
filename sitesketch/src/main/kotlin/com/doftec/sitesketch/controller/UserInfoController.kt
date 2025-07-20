package com.doftec.sitesketch.controller

import com.doftec.sitesketch.Utils.JwtUtil
import com.doftec.sitesketch.dto.AuthRequest
import com.doftec.sitesketch.dto.AuthResponse
import com.doftec.sitesketch.model.User
import com.doftec.sitesketch.repository.UserRepository
import com.doftec.sitesketch.service.DatabaseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.naming.AuthenticationException

@RestController

class UserInfoController(private val userRepository: UserRepository,
                         private val databaseService: DatabaseService,
                         private val authenticationManager: AuthenticationManager,
                         private val jwtUtil: JwtUtil) {

    @PostMapping("/register")
    fun addUser(@RequestBody user: User): ResponseEntity<String> {
        if (userRepository.findByEmail(user.email) != null) {
            return ResponseEntity.badRequest().body("User already exists")
        }

         databaseService.addUser(user)

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully")
    }
    @PostMapping("/login")
    fun login(@RequestBody authRequest: User): ResponseEntity<String> {
        println("Login attempt: ${authRequest.email}")
        return  try {

            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(authRequest.email, authRequest.password)
            )
            val user = userRepository.findByEmail(authRequest.email)
                ?: return ResponseEntity.status(401).build()
            val token = jwtUtil.generateToken(authRequest.email,user.roles)
            ResponseEntity.ok(token)
        } catch (e: AuthenticationException) {
            println("Authentication failed: ${e.message}")
            ResponseEntity.status(401).build()
        }
    }
    @GetMapping("/verify")
    fun verify(@RequestParam token: String): ResponseEntity<String> {
        val user = userRepository.findByVerificationToken(token)
            ?: return ResponseEntity.badRequest().body("Invalid token")

        if (user.tokenExpiration!!.isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expired")
        }

        user.enabled = true
        user.verificationToken = null
        user.tokenExpiration = null
        userRepository.save(user)

        return ResponseEntity.ok("Email verified! You can now log in.")
    }
    @PostMapping("/validate")
    fun validateJwt(@RequestBody token: String): ResponseEntity<Boolean>{
        if(jwtUtil.validateToken(token)){
            return ResponseEntity.ok(true)
        }
        return ResponseEntity.badRequest().body(false)
    }
}