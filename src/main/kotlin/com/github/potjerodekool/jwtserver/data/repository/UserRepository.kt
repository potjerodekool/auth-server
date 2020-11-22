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

    fun findByUserId(userId: String): User?

    fun findByUserIdAndResetToken(userId: String, resetToken: String): User?

    fun existsUserByUserId(userId: String): Boolean

    @Modifying
    @Transactional
    @Query("UPDATE User SET password = :newPassword, resetToken = null, resetTokenTtl = null WHERE userId = :userId")
    fun updatePassword(@Param("newPassword") newPassword: String,
                       @Param("userId") userid: String)
}