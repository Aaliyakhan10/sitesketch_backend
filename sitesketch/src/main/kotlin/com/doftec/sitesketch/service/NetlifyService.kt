package com.doftec.sitesketch.service
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class NetlifyService(
    @Value("\${netlify.api-token}") private val apiToken: String
) {
    private val restTemplate = RestTemplate()

    fun createSiteForUser(userId: String): String {
        val url = "https://api.netlify.com/api/v1/sites"

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            add("Authorization", "Bearer $apiToken")
        }

        val body = mapOf("name" to "user-$userId-site")
        val request = HttpEntity(body, headers)

        val response = restTemplate.postForEntity(url, request, Map::class.java)
        return response.body?.get("id")?.toString() ?: throw IllegalStateException("Site creation failed")
    }

    fun deployZip(siteId: String, zipBytes: ByteArray): Map<String, Any> {
        val url = "https://api.netlify.com/api/v1/sites/$siteId/deploys"

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_OCTET_STREAM
            add("Authorization", "Bearer $apiToken")
        }

        val request = HttpEntity(zipBytes, headers)

        val response = restTemplate.postForEntity(url, request, Map::class.java)
        return response.body as? Map<String, Any>
            ?: throw IllegalStateException("Deployment failed")
    }
}