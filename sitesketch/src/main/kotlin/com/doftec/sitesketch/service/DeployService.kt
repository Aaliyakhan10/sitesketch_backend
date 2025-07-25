package com.doftec.sitesketch.service

import com.doftec.sitesketch.model.Resume
import com.doftec.sitesketch.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service

    class DeployService(
        private val userRepository: UserRepository,
        private val webClient: WebClient,
        private val objectMapper: ObjectMapper
    ) {
    fun deployUser(email: String, name: String): Mono<String> {
        val user = userRepository.findByEmail(email)
            ?: return Mono.error(RuntimeException("User not found"))

        val objectMapper = jacksonObjectMapper()

        val content: Resume? = user.content



        val html = user.code ?: "<html><body><h1>Empty code</h1></body></html>"
        val zipBytes = createZipFile("index.html", html, content)

        return createSite(name)
            .flatMap { siteId -> deployToSite(siteId, zipBytes) }
    }


    private fun createZipFile(filename: String, htmlContent: String, resume: Resume?): ByteArray {
        val outputStream = ByteArrayOutputStream()

        // Attempt to unescape HTML if it's a JSON string
        val cleanedHtml = try {
            // This will parse something like "\"<html>...</html>\"" into "<html>...</html>"
            objectMapper.readValue(htmlContent, String::class.java)
        } catch (ex: Exception) {
            // Fallback to raw if parsing fails
            htmlContent.replace("\\n", "\n").replace("\\t", "\t")
        }

        ZipOutputStream(outputStream).use { zip ->
            // Write HTML file to "public/index.html"
            zip.putNextEntry(ZipEntry("public/$filename"))
            zip.write(cleanedHtml.toByteArray())
            zip.closeEntry()

            // Write resume.json to "public/resume.json"
            zip.putNextEntry(ZipEntry("public/resume.json"))
            val resumeJson = objectMapper.writeValueAsString(resume ?: "")
            zip.write(resumeJson.toByteArray())
            zip.closeEntry()

            // Add netlify.toml to root
            val netlifyToml = """
            [build]
            publish = "public"
        """.trimIndent()
            zip.putNextEntry(ZipEntry("netlify.toml"))
            zip.write(netlifyToml.toByteArray())
            zip.closeEntry()
        }
        File("test.zip").writeBytes(outputStream.toByteArray())

        return outputStream.toByteArray()
    }



    private fun createSite(siteName: String): Mono<String> {
        val cleanName = siteName.lowercase().replace("[^a-z0-9-]".toRegex(), "-")
        val payload = mapOf("name" to cleanName)

        return webClient.post()
            .uri("/sites")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(Map::class.java)
            .map { it["id"] as String }
    }


    private fun deployToSite(siteId: String, zipBytes: ByteArray): Mono<String> {
        val form = LinkedMultiValueMap<String, Any>()
        val resource = object : ByteArrayResource(zipBytes) {
            override fun getFilename(): String = "site.zip"
        }
        form.add("file", resource)

        return webClient.post()
            .uri("/sites/$siteId/deploys")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(form))
            .retrieve()
            .onStatus({ it.isError }) { response ->
                response.bodyToMono(String::class.java).flatMap { body ->
                    println("Netlify Error Response: $body")
                    Mono.error(RuntimeException("Netlify API error: $body"))
                }
            }

            .bodyToMono(String::class.java)
            .doOnNext { println("Site created successfully: $it") }
            .doOnError { println("Error occurred: ${it.message}") }
    }


}