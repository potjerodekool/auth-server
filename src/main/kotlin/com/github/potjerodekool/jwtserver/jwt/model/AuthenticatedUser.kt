package com.github.potjerodekool.jwtserver.jwt.model

import org.springframework.security.core.GrantedAuthority
import java.util.*

class AuthenticatedUser(private val uuid: String,
                        private val password: String): JwtUser {

    override fun getUserId(): String {
        return uuid
    }

    override fun getLastPasswordResetDate(): Date? {
        TODO("Not yet implemented")
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getPassword(): String = password

    override fun getUsername(): String = uuid

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}