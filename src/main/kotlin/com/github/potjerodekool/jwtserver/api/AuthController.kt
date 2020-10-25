package com.github.potjerodekool.jwtserver.api

import com.github.potjerodekool.jwtserver.api.model.AuthenticationResponse
import com.github.potjerodekool.jwtserver.api.model.JwtAuthenticationRequest
import com.github.potjerodekool.jwtserver.api.model.SignupRequst
import com.github.potjerodekool.jwtserver.service.AccountService
import com.github.potjerodekool.jwtserver.service.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class AuthController(private val authenticationService: AuthenticationService,
                     private val accountService: AccountService) {

    @PostMapping("/authenticate", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun authenticate(@RequestBody request: JwtAuthenticationRequest): ResponseEntity<AuthenticationResponse> {
        try {
            val response = authenticationService.authenticate(
                    request.userName,
                    request.password
            )
            return ResponseEntity.ok(response)
        } catch (e: BadCredentialsException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("/signup", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun signUp(@RequestBody request: SignupRequst): ResponseEntity<String> {
        val errorMessage = accountService.signUp(
                request.userName,
                request.password
        )

        if (errorMessage == null) {
            return ResponseEntity.ok().build()
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorMessage)
        }
    }

}