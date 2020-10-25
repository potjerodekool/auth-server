package com.github.potjerodekool.jwtserver.data.repository

import com.github.potjerodekool.jwtserver.data.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRespositry : JpaRepository<User, Int> {

    fun findByUserid(userId: String): User?

    fun existsUserByUserid(userId: String): Boolean
}