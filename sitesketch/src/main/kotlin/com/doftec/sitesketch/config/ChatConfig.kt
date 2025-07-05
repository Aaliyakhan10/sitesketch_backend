package com.doftec.sitesketch.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.model.ApiKey
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestClient


import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ChatConfig {
    @Value("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")
    private lateinit var apiKey: String

    @Bean
    fun openRouterRestClient(): RestClient {
        val restClient = RestClient.builder()
            .baseUrl("https://openrouter.ai/api/v1")
            .defaultHeaders { headers ->
                headers.add(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
                headers.add("HTTP-Referer", "http://localhost:8080")
                headers.add("X-Title", "SiteSketch Local")
                headers.contentType = MediaType.APPLICATION_JSON
            }
        return restClient.build()

    }
}



