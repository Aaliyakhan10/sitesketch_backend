package com.doftec.sitesketch.controller

import com.doftec.sitesketch.dto.SaveToDatabaseRequest
import com.doftec.sitesketch.model.Resume
import com.doftec.sitesketch.service.Aiservice
import com.doftec.sitesketch.service.DatabaseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class Aicontroller(private val aiservice: Aiservice,private val databaseService: DatabaseService) {

    @PostMapping("/ai-code")
    fun callAi(@RequestBody resume: Resume): ResponseEntity<String> {
        val email=databaseService.getCurrentUserEmail()
        if(email?.isNotEmpty() == true ) {
            databaseService.addResume(resume, email)
            return ResponseEntity.ok(aiservice.callAi(resume))
        }
        return  ResponseEntity.ok("Error")
    }
    @PostMapping("/resume-update")
    fun updateResume(@RequestBody resume: Resume): ResponseEntity<String> {
        val email=databaseService.getCurrentUserEmail()
        if(email?.isNotEmpty() == true ) {
             databaseService.addResume(resume, email)

            return ResponseEntity.ok("Save to database")
        }
        return  ResponseEntity.ok("Error")
    }
    @PostMapping("/code-push")
    fun updateCode(@RequestBody saveToDatabaseRequest: SaveToDatabaseRequest): ResponseEntity<String> {
        val email=databaseService.getCurrentUserEmail()
        if(email?.isNotEmpty() == true ) {
            databaseService.addCode(saveToDatabaseRequest.code,email)
            databaseService.addResume(saveToDatabaseRequest.resume,email)

            return ResponseEntity.ok("Save to database")
        }
        return  ResponseEntity.badRequest().body("Error")
    }
    @GetMapping("/get-code")
    fun getCode(): ResponseEntity<String>{
        val email=databaseService.getCurrentUserEmail()
        if(email?.isNotEmpty() == true ) {
           val code=  databaseService.getCode(email)
            return ResponseEntity.ok(code)
        }
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }
    @GetMapping("/get-resume")
    fun getResume(): ResponseEntity<Resume> {
        val email = databaseService.getCurrentUserEmail()
        return if (email?.isNotEmpty() == true) {
            val resume = databaseService.getResume(email)
            ResponseEntity.ok(resume)
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @GetMapping("/whoami")
    fun whoAmI(): Map<String, Any> {
        val auth = SecurityContextHolder.getContext().authentication
        return mapOf(
            "name" to auth.name,
            "authenticated" to auth.isAuthenticated,
            "authorities" to auth.authorities.map { it.authority }
        )
    }
}