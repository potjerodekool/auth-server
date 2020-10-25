package com.github.potjerodekool.jwtserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication(scanBasePackages = ["com.github.potjerodekool.jwtserver"])
class Application {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}


