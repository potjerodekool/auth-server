package com.github.potjerodekool.jwtserver.data.repository

import com.github.potjerodekool.jwtserver.data.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository : JpaRepository<User, Int> {

    fun findUserByEmail(email: String): User?

    fun findByEmailAndResetToken(email: String, resetToken: String): User?

    fun existsUserByEmail(email: String): Boolean

    @Modifying
    @Transactional
    @Query("UPDATE User SET password = :newPassword, resetToken = null, resetTokenTtl = null WHERE email = :email")
    fun updatePassword(@Param("newPassword") newPassword: String,
                       @Param("email") email: String)
}