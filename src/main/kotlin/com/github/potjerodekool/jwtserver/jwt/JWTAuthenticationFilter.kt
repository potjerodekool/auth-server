package com.github.potjerodekool.jwtserver.jwt

import com.github.potjerodekool.jwtserver.service.AccountService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(private val jwtService: JwtService,
                              private val accountService: AccountService) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        var authToken: String? = request.getHeader("Authorization")

        if (authToken != null) {
            if (authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7)
            }

            try {
                val username = jwtService.getUsernameFromToken(authToken)

                if (username != null && SecurityContextHolder.getContext().authentication == null) {

                    // It is not compelling necessary to load the use details from the database. You could also store the information
                    // in the token and read it from it. It's up to you ;)
                    val userDetails = this.accountService.loadUserByUsername(username)

                    // For simple validation it is completely sufficient to just check the token integrity. You don't have to call
                    // the database compellingly. Again it's up to you ;)
                    if (jwtService.validateToken(authToken, userDetails)) {
                        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authentication
                    }
                }
            } catch (e : Exception) {
                response.sendError(HttpStatus.UNAUTHORIZED.value())
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}