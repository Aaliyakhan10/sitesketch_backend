package com.doftec.sitesketch.controller


import com.doftec.sitesketch.dto.DeployRequest
import com.doftec.sitesketch.service.DatabaseService
import com.doftec.sitesketch.service.DeployService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/deploy")
class DeployController(
    private val deployService: DeployService,
    private val databaseService: DatabaseService
) {
    @PostMapping
    fun deployUser(@RequestBody deployRequest: DeployRequest): Mono<String> {
        val email = databaseService.getCurrentUserEmail()
        val payload = mapOf("name" to deployRequest.name)

        return if (!email.isNullOrEmpty()) {
            deployService.deployUser(email, payload.toString())
                .map { "✅ Site deployed at: $it" }
        } else {
            Mono.just("❌ Error: User not logged in or email missing.")
        }
    }
}
