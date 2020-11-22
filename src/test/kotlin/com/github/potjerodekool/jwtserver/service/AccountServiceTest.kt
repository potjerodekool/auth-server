package com.github.potjerodekool.jwtserver.service

import com.github.potjerodekool.jwtserver.api.model.ResetPasswordRequest
import com.github.potjerodekool.jwtserver.data.entity.User
import com.github.potjerodekool.jwtserver.data.repository.UserRepository
import com.github.potjerodekool.jwtserver.jwt.JwtService
import com.github.potjerodekool.jwtserver.jwt.model.AuthenticatedUser
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.time.LocalDateTime
import java.util.*

internal class AccountServiceTest {

    @InjectMockKs
    private lateinit var accountService: AccountService

    @MockK
    private lateinit var userRepositoryMock: UserRepository

    @MockK
    private lateinit var jwtServiceMock: JwtService

    @MockK
    private lateinit var javaMailSender: JavaMailSender

    private val mailUser = "evert"

    private val loginUrl = "http://localhost:8080/login"

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun loadUserByUserName() {
        val user = User(
                id = 121,
                userId = "Evert",
                password = "90934-0",
        )

        every {
            userRepositoryMock.findByUserId("evert")
        } returns user

        val loadedUser = accountService.loadUserByUsername("evert")

        assertTrue(loadedUser is AuthenticatedUser)
        assertEquals("evert", loadedUser.username)
    }

    @Test
    fun `loadUserByUserName with no username`() {
        val exception = assertThrows(UsernameNotFoundException::class.java) {
            accountService.loadUserByUsername(null)
        }

        assertEquals("User could not be found", exception.message)
    }

    @Test
    fun `loadUserByUserName with unknown user`() {
        every {
            userRepositoryMock.findByUserId(any())
        } returns null

        val exception = assertThrows(UsernameNotFoundException::class.java) {
            accountService.loadUserByUsername("Piet")
        }

        assertEquals("User Piet could not be found", exception.message)
    }

    @Test
    fun signUp() {
        every {
            userRepositoryMock.existsUserByUserId(any())
        } returns false

        val userSlot = slot<User>()

        every {
            userRepositoryMock.save(
                    capture(userSlot)
            )
        } answers { call ->
            call.invocation.args.first() as User
        }

        assertNull(accountService.signUp("evert", "test"))

        val savedUser = userSlot.captured
        assertEquals("evert", savedUser.userId)
        assertNotNull(savedUser.password)
        assertNotEquals("test", savedUser.password)
    }

    @Test
    fun signUpFailedAccountNotAvailable() {
        every {
            userRepositoryMock.existsUserByUserId("evert")
        } returns true

        assertEquals("Account not available", accountService.signUp("evert", "test"))

        verify(exactly = 0) {
            userRepositoryMock.save(
                    any()
            )
        }
    }

    @Test
    fun `requestPasswordReset unknown user`() {
        every {
            userRepositoryMock.findByUserId(any())
        } returns null

        accountService.requestPasswordReset("Piet")

        verify {
            javaMailSender wasNot Called
        }
    }

    @Test
    fun resetPassword() {
        val resetToken = UUID.randomUUID().toString()

        val user = User(
                1,
                "evert",
                "oldpassword",
                resetToken,
                LocalDateTime.now().plusMinutes(1)
        )

        every {
            userRepositoryMock.findByUserIdAndResetToken(
                    "evert",
                    resetToken
            )
        } returns user

        val passwordSlot = slot<String>()

        every {
            userRepositoryMock.updatePassword(
                    capture(passwordSlot),
                    "evert"
            )
        } just runs

        assertNull(accountService.resetPassword(
                ResetPasswordRequest(
                        userId = "evert",
                        newPassword = "newtest",
                        resetToken = resetToken
                )
        ))

        assertNotNull(passwordSlot.captured)
        assertNotEquals("newtest", passwordSlot.captured)
    }

    @Test
    fun testResetPasswordWithAuth() {
        every {
            jwtServiceMock.getUsernameFromToken(
                    any()
            )
        } returns "evert"

        val passwordSlot = slot<String>()

        every {
            userRepositoryMock.updatePassword(
                    capture(passwordSlot),
                    "evert"
            )
        } just runs


        assertNull(accountService.resetPassword(
                ResetPasswordRequest(
                        userId = "evert",
                        newPassword = "newtest",
                        resetToken = "8rueoi34045"
                ),
                ""
        ))

        assertNotNull(passwordSlot.captured)
        assertNotEquals("newtest", passwordSlot.captured)
    }
}