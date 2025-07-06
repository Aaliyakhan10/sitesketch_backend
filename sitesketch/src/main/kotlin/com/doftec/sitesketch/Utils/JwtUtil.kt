package com.doftec.sitesketch.Utils
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.Claims
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {
    private val BASE64_SECRET= "5M7pBjGm9fNqzD2YWzAR8ku5H4xVZ1aBg9Xm2rcL3KQoTzNViAyt"
    private val EXPIRATION_TIME = 1000 * 60 * 60 // 1 hour


    private val key: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(BASE64_SECRET))

    fun generateToken(username: String, roles: List<String>): String {
        val claims = HashMap<String, Any>()
        claims["roles"] = roles
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractUsername(token: String): String {
        return extractClaims(token).subject
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = extractClaims(token)
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun extractClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
