package com.doftec.sitesketch.filter

import com.doftec.sitesketch.Utils.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val path = request.servletPath
        if (path == "/login" || path == "/register") {
            chain.doFilter(request, response)
            return
        }
        val authHeader = request.getHeader("Authorization")
        val token = authHeader?.takeIf { it.startsWith("Bearer ") }?.substring(7)

        token?.let {

            if (jwtUtil.validateToken(it)) {
                val username = jwtUtil.extractUsername(it)
                val claims = jwtUtil.extractClaims(it)
                val roles = (claims["roles"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                println("JWT roles from token: $roles")
                println("Authentication set: ${SecurityContextHolder.getContext().authentication}")



                val authorities = roles.map { role -> SimpleGrantedAuthority("ROLE_$role") }

                val auth = UsernamePasswordAuthenticationToken(username, null, authorities)
                auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = auth
            }
        }


        chain.doFilter(request, response)
    }
}