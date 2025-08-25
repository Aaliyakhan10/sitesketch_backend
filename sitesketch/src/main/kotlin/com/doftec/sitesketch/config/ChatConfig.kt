package com.doftec.sitesketch.config


import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

@Configuration
class ChatConfig {

    @Value("\${OPEN_ROUTER_API}")
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



