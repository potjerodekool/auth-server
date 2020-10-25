package com.github.potjerodekool.jwtserver.jwt.model

import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface JwtUser: UserDetails {

    fun getUserId(): String?

    fun getLastPasswordResetDate(): Date?

}