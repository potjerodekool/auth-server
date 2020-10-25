package com.github.potjerodekool.jwtserver.jwt

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime

internal class JwtServiceTest {

    /*
    @InjectMockKs
    private lateinit var jwtService: JwtService

    private val secretKey = "secret"

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }
     */

    @Test
    fun createJWT() {
        /*
        val start = LocalDateTime.now()

        val token = jwtService.createAccessToken("evert", "mysubject")
        val decodes = jwtService.decodeJWT(token)
        assertEquals("evert", decodes["iss"])
        assertEquals("mysubject", decodes["sub"])
         */
    }
}