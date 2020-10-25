package com.github.potjerodekool.jwtserver.service

import com.github.potjerodekool.jwtserver.api.model.AuthenticationResponse
import com.github.potjerodekool.jwtserver.jwt.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.stereotype.Service
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

@Service
class AuthenticationService(private var authenticationManager: AuthenticationManager,
                            private val jwtService: JwtService) {

    fun authenticate(userName: String, password: String): AuthenticationResponse {
        val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(userName, password))
        SecurityContextHolder.getContext().authentication = authentication
        val user = authentication.principal

        val token = jwtService.createAccessToken(userName, userName)
        val refreshToken = jwtService.getRefreshToken()
        return AuthenticationResponse(token, refreshToken)
    }
}