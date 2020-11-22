package com.github.potjerodekool.jwtserver

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication(scanBasePackages = ["com.github.potjerodekool.jwtserver"])
class Application {

    @Value("\${spring.mail.host}")
    private val mailServerHost: String? = null

    @Value("\${spring.mail.port}")
    private val mailServerPort: Int = 0

    @Value("\${spring.mail.username}")
    private val mailServerUsername: String? = null

    @Value("\${spring.mail.password}")
    private val mailServerPassword: String? = null

    @Value("\${spring.mail.properties.mail.transport.protocol}")
    private val mailSernderProtocol: String? = null

    @Value("\${spring.mail.properties.mail.smtp.auth}")
    private val mailServerAuth: String? = null

    @Value("\${spring.mail.properties.mail.smtp.starttls.enable}")
    private val mailServerStartTls: String? = null

    @Value("\${spring.mail.properties.mail.smtps.ssl.checkserveridentity}")
    private val mailServerCheckserveridentity: String? = null

    @Value("\${spring.mail.properties.mail.smtps.ssl.trust}")
    private val mailServerSslTrust: String? = null

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun getJavaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = mailServerHost
        mailSender.port = mailServerPort
        mailSender.username = mailServerUsername
        mailSender.password = mailServerPassword

        val props = mailSender.javaMailProperties
        props["mail.transport.protocol"] = mailSernderProtocol
        props["mail.smtp.auth"] = mailServerAuth
        props["mail.smtp.starttls.enable"] = mailServerStartTls
        props["mail.smtps.ssl.checkserveridentity"] = mailServerCheckserveridentity
        props["mail.smtps.ssl.trust"] = mailServerSslTrust

        return mailSender
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}