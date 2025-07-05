package com.doftec.sitesketch.controller

import com.doftec.sitesketch.model.Resume
import com.doftec.sitesketch.service.Aiservice
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
@RestController
class Aicontroller(private val aiservice: Aiservice) {

    @PostMapping("/ai-code")
    fun callAi(@RequestBody resume: Resume): ResponseEntity<String> {

        return ResponseEntity.ok(aiservice.callAi(resume))
    }
    @PostMapping("/resume-update")
    fun Update(@RequestBody resume: Resume): ResponseEntity<String> {

        return ResponseEntity.ok(aiservice.callAi(resume))
    }
}