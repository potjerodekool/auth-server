package com.github.potjerodekool.jwtserver.api.model

data class JwtAuthenticationRequest(val userName: String, val password: String)