package com.github.potjerodekool.jwtserver.jwt

import com.github.potjerodekool.jwtserver.Application
import com.github.potjerodekool.jwtserver.service.AccountService
import com.github.potjerodekool.jwtserver.service.AuthenticationService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.AuthenticationManager

@SpringBootTest(classes = [Application::class])
internal class FullTest {

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var authenticationService: AuthenticationService

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Test
    fun test() {
        /*
        val errorMessage = accountService.signUp("Evert", "Test")
        assertNull(errorMessage)

        val result = authenticationService.authenticate("Evert", "Test")

        assertNotNull(result.accessToken)
        assertNotNull(result.refreshToken)
        */
    }
}