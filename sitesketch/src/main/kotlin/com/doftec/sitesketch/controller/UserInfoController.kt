package com.doftec.sitesketch.controller

import com.doftec.sitesketch.model.User
import com.doftec.sitesketch.repository.UserRepository
import com.doftec.sitesketch.service.DatabaseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController

class UserInfoController(private val userRepository: UserRepository, private val databaseService: DatabaseService,private val authenticationManager: AuthenticationManager) {

    @PostMapping("/register")
    fun addUser(@RequestBody user: User): ResponseEntity<String> {
        if (userRepository.findByEmail(user.email) != null) {
            return ResponseEntity.badRequest().body("User already exists")
        }

         databaseService.addUser(user)

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully")
    }
    @PostMapping("/login")
    fun login(@RequestBody user: User): ResponseEntity<String> {
        return try {

            val auth = UsernamePasswordAuthenticationToken(user.email, user.password)
            authenticationManager.authenticate(auth)
            ResponseEntity.ok("Login successful")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")
        }
    }
}