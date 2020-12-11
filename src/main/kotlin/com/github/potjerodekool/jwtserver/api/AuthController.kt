package com.github.potjerodekool.jwtserver.api

import com.github.potjerodekool.jwtserver.api.model.*
import com.github.potjerodekool.jwtserver.service.AccountService
import com.github.potjerodekool.jwtserver.service.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.logging.Level
import java.util.logging.Logger

@CrossOrigin
@Controller
class AuthController(private val authenticationService: AuthenticationService,
                     private val accountService: AccountService) {

    private companion object {
        private const val TOKEN_PREFIX = "Bearer "
        private val LOGGER = Logger.getLogger(AuthController::class.java.name)
    }

    @PostMapping("/authenticate", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun authenticate(@RequestBody request: JwtAuthenticationRequest): ResponseEntity<AuthenticationResponse> {
        return try {
            val response = authenticationService.authenticate(
                    request.email,
                    request.password
            )
            ResponseEntity.ok(response)
        } catch (e: BadCredentialsException) {
            LOGGER.log(Level.INFO, "Login failed for ${request.email}, bad credentials")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("/signup", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun signUp(@RequestBody request: SignupRequst): ResponseEntity<String> {
        val errorMessage = accountService.signUp(
                request.email,
                request.password
        )

        return if (errorMessage == null) ResponseEntity.ok().build() else
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage)
    }

    @GetMapping("/refreshtoken")
    fun refreshToken(@RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<AuthenticationResponse> {
        val response = authenticationService.refreshToken(refreshTokenRequest.refreshToken)
        return if (response == null) ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() else ResponseEntity.ok(response)
    }

    @PatchMapping("/resetpassword")
    fun resetPassword(@RequestBody resetPasswordRequest: ResetPasswordRequest,
                      @RequestHeader("Authorization", required = false) authorization: String?): ResponseEntity<String> {
        return when {
            authorization == null -> {
                doResetPasswordNoAuth(resetPasswordRequest)
            }
            authorization.startsWith(TOKEN_PREFIX).not() -> {
                ResponseEntity.badRequest().build()
            }
            else -> {
                doResetPassword(resetPasswordRequest, authorization)
            }
        }
    }

    private fun doResetPasswordNoAuth(resetPasswordRequest: ResetPasswordRequest): ResponseEntity<String> {
        if (resetPasswordRequest.resetToken.isNullOrEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        val errorMessage = accountService.resetPassword(resetPasswordRequest)
        return if (errorMessage != null) ResponseEntity.badRequest().body(errorMessage) else ResponseEntity.ok().build()
    }

    private fun doResetPassword(resetPasswordRequest: ResetPasswordRequest,
                              authorization: String): ResponseEntity<String> {
        val authToken = authorization.substring(TOKEN_PREFIX.length)
        val errorMessage = accountService.resetPassword(resetPasswordRequest, authToken)
        return if (errorMessage != null) ResponseEntity.badRequest().body(errorMessage) else ResponseEntity.ok().build()
    }

    @PostMapping("/requestresetPassword")
    fun requestPasswordResetWithAuthToken(@RequestBody() request: RequestPasswordResetRequest): ResponseEntity<String> {
        accountService.requestPasswordReset(request.email)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/updatepassword")
    fun updatepassword(@RequestBody resetPasswordRequest: ResetPasswordRequest): ResponseEntity<String> {
        val errorMessage = accountService.resetPassword(resetPasswordRequest)
        return if (errorMessage != null) ResponseEntity.badRequest().build()  else ResponseEntity.ok().build()
    }
}