package com.doftec.sitesketch.controller


import com.doftec.sitesketch.dto.DeployRequest
import com.doftec.sitesketch.repository.UserRepository
import com.doftec.sitesketch.service.DatabaseService
import com.doftec.sitesketch.service.DeployService
import com.doftec.sitesketch.service.DeploymentService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.time.Duration

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

    @RestController

    class DeploymentController(
        private val deploymentService: DeploymentService,
        private val deployService: DeployService,
        private val databaseService: DatabaseService,
        private val userRepository: UserRepository
    ) {

        @PostMapping("/deploy")
        fun deployUserSite(@RequestBody deployRequest: DeployRequest): ResponseEntity<Map<String, String>> {
            val email = databaseService.getCurrentUserEmail()
            val userId= email?.split("@").toString()
           return if(email?.isNotEmpty() ==true) {
               val user = deploymentService.deployForUser(email)
               ResponseEntity.ok(
                   mapOf(
                       "message" to "Deployment successful",
                       "url" to (user.url ?: "")
                   )
               )
           }else{
               ResponseEntity.ok(
                   mapOf(
                       "message" to "Deployment failed",
                       "url" to ("")
                   )
               )
           }
        }

        @GetMapping("/generate-zip")
        fun generateSite(): ResponseEntity<ByteArray> {
            val email = databaseService.getCurrentUserEmail()

            return if (!email.isNullOrBlank()) {
                val user = userRepository.findByEmail(email)
                ResponseEntity.ok(deployService.createZipFile(user?.code, user?.content))
            } else {
                // Return an empty ByteArray with UNAUTHORIZED status
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ByteArray(0))
            }
        }

    }



