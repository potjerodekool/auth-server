package com.github.potjerodekool.jwtserver.service

import com.github.potjerodekool.jwtserver.data.entity.User
import com.github.potjerodekool.jwtserver.data.repository.UserRespositry
import com.github.potjerodekool.jwtserver.jwt.model.AuthenticatedUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service


@Service
class AccountService(private val userRespositry: UserRespositry): UserDetailsService {

    override fun loadUserByUsername(userName: String?): UserDetails {
         if (userName != null) {
            val user = userRespositry.findByUserid(userName)

            if  (user != null) {
                return AuthenticatedUser(userName, user.password)
            } else {
                throw UsernameNotFoundException("User $userName could not be found")
            }
         } else {
             throw UsernameNotFoundException("User could not be found")
         }
    }

    fun signUp(userName: String, password: String): String? {
        return if (userRespositry.existsUserByUserid(userName)) {
            "Username allready taken"
        } else {
            val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
            userRespositry.save(
                    User(
                            userid = userName,
                            password = passwordHash
                    )
            )
            null
        }
    }
}