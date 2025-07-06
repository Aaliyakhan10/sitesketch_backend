package com.doftec.sitesketch.repository

import com.doftec.sitesketch.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

 interface UserRepository: MongoRepository<User, String> {
     fun findByEmail(email: String): User?
     fun findByVerificationToken(token: String): User?
}