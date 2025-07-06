package com.doftec.sitesketch.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("users")
data class User(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val email: String,
    var password: String,
    val name: String?=null,
    var content: Resume?=null,
    var code: String?=null,
    val roles: List<String> = listOf("USER"),
    var enabled: Boolean = false,
    var verificationToken: String? = null,
    var tokenExpiration: LocalDateTime? = null


    )

