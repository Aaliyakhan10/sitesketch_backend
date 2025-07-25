package com.doftec.sitesketch.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
@Configuration
class DeployConfig (

    private val webClientBuilder: WebClient.Builder
){

    private val NETLIFY_ACCESS_TOKEN = "Bearer nfp_qFrpVdkyqQKudzp3tXQyYmMu3k6bahNB2012"
    @Bean
    fun netlifyWebClient(builder: WebClient.Builder): WebClient {
        return builder
            .baseUrl("https://api.netlify.com/api/v1")
            .defaultHeader(HttpHeaders.AUTHORIZATION, NETLIFY_ACCESS_TOKEN)
            .build()
    }
}