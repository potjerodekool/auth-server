package com.github.potjerodekool.jwtserver.jwt.model

import org.springframework.security.core.GrantedAuthority
import java.util.*

class AuthenticatedUser(private val userName: String,
                        private val password: String): JwtUser {

    init {
        println("init")
    }

    override fun getUserId(): String {
        TODO("Not yet implemented")
    }

    override fun getLastPasswordResetDate(): Date? {
        TODO("Not yet implemented")
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getPassword(): String = password

    override fun getUsername(): String = userName

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}