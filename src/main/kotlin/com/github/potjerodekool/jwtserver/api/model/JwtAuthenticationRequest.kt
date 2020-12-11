package com.github.potjerodekool.jwtserver.api.model

data class JwtAuthenticationRequest(val email: String, val password: String)