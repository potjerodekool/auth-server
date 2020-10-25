package com.github.potjerodekool.jwtserver.data.repository

import com.github.potjerodekool.jwtserver.data.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Int> {

    @Transactional
    @Modifying
    @Query("UPDATE RefreshToken set token = :newToken where id = 1")
    fun updateRefreshToken(@Param("newToken") newToken: String)
}