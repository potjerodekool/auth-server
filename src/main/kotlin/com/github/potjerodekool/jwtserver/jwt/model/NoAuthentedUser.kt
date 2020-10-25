package com.github.potjerodekool.jwtserver.jwt.model

import org.springframework.security.core.GrantedAuthority
import java.util.*

object NoAuthentedUser : JwtUser {

    override fun getUserId() = null

    override fun getLastPasswordResetDate(): Date? = null

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

    override fun getPassword() = null

    override fun getUsername() = null

    override fun isAccountNonExpired(): Boolean = false

    override fun isAccountNonLocked(): Boolean = false

    override fun isCredentialsNonExpired(): Boolean = false

    override fun isEnabled(): Boolean = false
}