package com.github.potjerodekool.jwtserver.service

import com.github.potjerodekool.jwtserver.api.model.ResetPasswordRequest
import com.github.potjerodekool.jwtserver.data.entity.User
import com.github.potjerodekool.jwtserver.data.repository.UserRepository
import com.github.potjerodekool.jwtserver.jwt.JwtService
import com.github.potjerodekool.jwtserver.jwt.model.AuthenticatedUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class AccountService(private val userRepository: UserRepository,
                     private val jwtService: JwtService,
                     private val mailSender: JavaMailSender,
                     @Value("\${spring.mail.username}") private val mailUser: String,
                     @Value("\${login.url}") private val loginUrl: String): UserDetailsService {

    override fun loadUserByUsername(userName: String?): UserDetails {
         if(userName != null) {
            val user = userRepository.findByUserId(userName)

            if(user != null) {
                return AuthenticatedUser(userName, user.password)
            } else {
                throw UsernameNotFoundException("User $userName could not be found")
            }
         } else {
             throw UsernameNotFoundException("User could not be found")
         }
    }

    fun signUp(userName: String, password: String): String? {
        return if(userRepository.existsUserByUserId(userName)) {
            "Account not available"
        } else {
            val passwordHash = hashPassword(password)
            userRepository.save(
                    User(
                            userId = userName,
                            password = passwordHash
                    )
            )
            null
        }
    }

    private fun hashPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())

    @Transactional
    fun requestPasswordReset(userId: String) {
        val user = userRepository.findByUserId(userId)

        if (user == null) {
            return
        }

        val resetToken = UUID.randomUUID().toString()
        val ttl = LocalDateTime.now().plusHours(4)

        user.resetToken = resetToken
        user.resetTokenTtl = ttl

        val mail = mailSender.createMimeMessage()
        val mailHelper = MimeMessageHelper(mail)
        mailHelper.setText(createResetpasswordMessage(user.userId, resetToken), true)
        mailHelper.setTo(user.userId)
        mailHelper.setSubject("Wachtwoord opnieuw instellen")
        mailHelper.setFrom(mailUser)
        mailSender.send(mail)
    }

    private fun createResetpasswordMessage(email: String, validationToken: String): String {
        val template = javaClass.classLoader
                .getResource("resetpassword-mail-template.html").readText()
        val url = "$loginUrl/changepassword?email=$email&validationtoken=$validationToken"
        return template.replace("%URL%", url)
    }

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest, authToken: String): String? {
        val userId = jwtService.getUsernameFromToken(authToken)

        if (userId != resetPasswordRequest.userId) {
            return "Tried to reset password of somebody else"
        }

        resetPassword(userId, resetPasswordRequest.newPassword)
        return null
    }

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest): String? {
        if(resetPasswordRequest.resetToken == null) {
            return "Invalid reset token"
        }

        val user = userRepository.findByUserIdAndResetToken(
                resetPasswordRequest.userId,
                resetPasswordRequest.resetToken
        )

        if(user == null || user.resetToken == null || user.resetTokenTtl == null) {
            return "Failed to reset password"
        }

        val now = LocalDateTime.now()

        if(resetPasswordRequest.resetToken != user.resetToken || now.isAfter(user.resetTokenTtl)) {
            return "Failed to reset password"
        }

        resetPassword(user.userId, resetPasswordRequest.newPassword)
        return null
    }

    private fun resetPassword(userId: String,
                              newPassword: String) {
        val passwordHash = hashPassword(newPassword)
        userRepository.updatePassword(passwordHash, userId)
    }

}