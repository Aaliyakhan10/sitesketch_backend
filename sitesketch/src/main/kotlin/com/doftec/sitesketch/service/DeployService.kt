package com.doftec.sitesketch.service

import com.doftec.sitesketch.model.Resume
import com.doftec.sitesketch.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.context.properties.bind.Bindable.mapOf
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.time.Duration
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

@Service
class DeployService(

    private val objectMapper: ObjectMapper
) {

    fun createZipFile(htmlContent: String?, resume: Resume?): ByteArray {
        val cleanedHtml = htmlContent?.replace("\\n", "\n")?.replace("\\t", "\t")
            ?: "<html><body><h1>Empty code</h1></body></html>"

        val outputStream = ByteArrayOutputStream()
        ZipOutputStream(outputStream).use { zip ->
            zip.putNextEntry(ZipEntry("index.html"))
            zip.write(cleanedHtml.toByteArray())
            zip.closeEntry()

            val resumeJson = objectMapper.writeValueAsString(resume ?: "")
            zip.putNextEntry(ZipEntry("resume.json"))
            zip.write(resumeJson.toByteArray())
            zip.closeEntry()
        }
        return outputStream.toByteArray()
    }
}