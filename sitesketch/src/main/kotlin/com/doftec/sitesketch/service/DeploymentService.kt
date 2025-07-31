package com.doftec.sitesketch.service

import com.doftec.sitesketch.model.User
import com.doftec.sitesketch.repository.UserRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service

@Service
class DeploymentService(
    private val netlifyService: NetlifyService,
private  val userRepository: UserRepository,
    private val deployService: DeployService,
    private val mongoTemplate: MongoTemplate,
    private val databaseService: DatabaseService
) { fun deployForUser(userId: String): User {
    val user = userRepository.findByEmail(userId)

    // Create zip from user's content
    val zipBytes = deployService.createZipFile(user?.code, user?.content)

    // Create site if doesn't exist
    val siteId = user?.siteId ?: run {
        val newSiteId = netlifyService.createSiteForUser(userId)
        updateUserField(userId, "siteId", newSiteId)
        newSiteId
    }

    // Deploy zip
    val deployment = netlifyService.deployZip(siteId, zipBytes)

    // Update user info
    return updateUserDeployInfo(
        email=userId,
        userId = userId,
        deployId = deployment["id"]?.toString(),
        url = deployment["deploy_ssl_url"]?.toString(),
        status = "DEPLOYED"
    )
}




    private fun updateUserField(userId: String, field: String, value: Any) {
        mongoTemplate.updateFirst(
            Query.query(Criteria.where("_id").`is`(userId)),
            Update().set(field, value),
            User::class.java
        )
    }
    private fun updateUserDeployInfo(
        userId: String,
        deployId: String?,
        url: String?,
        status: String,
        email: String
    ): User {
        val user = userRepository.findByEmail(email)

        return user.let {
            it?.deployId = deployId
            it?.url = url
            it?.status = status
            databaseService.saveUser(it)
        }
    }

}