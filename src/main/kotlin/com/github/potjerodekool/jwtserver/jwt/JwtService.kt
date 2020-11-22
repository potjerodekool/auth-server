package com.github.potjerodekool.jwtserver.jwt

import com.github.potjerodekool.jwtserver.data.entity.RefreshToken
import com.github.potjerodekool.jwtserver.data.repository.RefreshTokenRepository
import com.github.potjerodekool.jwtserver.jwt.model.JwtUser
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

@Service
class JwtService(@Value("\${jwt.secretkey}") private val secretKey: String,
                 private val refreshTokenRepository: RefreshTokenRepository) {

    companion object {
        private const val CLAIM_KEY_CREATED = "created"
        private const val ACCESS_TOKEN_TTL: Long = 1000 * 60 * 15
        private const val REFRESH_TOKEN_TTL: Long = 1000 * 60 * 60 * 24
    }

    private var refreshToken: String = ""

    @PostConstruct
    fun postConstruct() {
        val refreshTokenOptional = refreshTokenRepository.findById(1)

        if (refreshTokenOptional.isPresent) {
            val refreshTokenRow = refreshTokenOptional.get()

            if (isTokenExpired(refreshTokenRow.token)) {
                updateRefreshToken()
            } else {
                refreshToken = refreshTokenRow.token
            }
        } else {
            updateRefreshToken()
        }
    }

    private fun refreshRefreshTokenIfNeeded() {
        if (refreshToken == "") {
            updateRefreshToken()
        } else if (isTokenExpired(refreshToken)) {
            updateRefreshToken()
        }
    }

    private fun updateRefreshToken() {
        refreshToken = generateRefreshToken()

        val exists = refreshTokenRepository.existsById(1)

        if (exists) {
            refreshTokenRepository.updateRefreshToken(refreshToken)
        } else {
            refreshTokenRepository.save(RefreshToken(token = refreshToken))
        }
    }

    private final fun generateRefreshToken(): String = createJWT(
            "refreshToken", "refreshToken", REFRESH_TOKEN_TTL
    )

    fun getRefreshToken(): String = refreshToken

    fun createAccessToken(issuer: String, subject: String): String {
        return createJWT(issuer, subject, ACCESS_TOKEN_TTL)
    }

    private fun createJWT(issuer: String, subject: String, ttl: Long): String {
        val id = UUID.randomUUID().toString()

        //The JWT signature algorithm we will be using to sign the token
        val signatureAlgorithm: SignatureAlgorithm = SignatureAlgorithm.HS256
        val nowMillis = System.currentTimeMillis()
        val now = Date(nowMillis)

        //We will sign our JWT with our ApiKey secret
        val apiKeySecretBytes: ByteArray = DatatypeConverter.parseBase64Binary(secretKey)
        val signingKey = SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.jcaName)

        //Let's set the JWT Claims
        val builder: JwtBuilder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey)

        val expMillis = nowMillis + ttl
        val exp = Date(expMillis)
        builder.setExpiration(exp)

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact()
    }

    fun decodeJWT(jwt: String?): Claims {
        //This line will throw an exception if it is not a signed JWS (as expected)
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .parseClaimsJws(jwt).body
    }

    fun validateToken(token: String?, userDetails: UserDetails): Boolean {
        if (token == null) {
            return false
        }

        val user = userDetails as JwtUser
        val username = getUsernameFromToken(token)
        val created = getCreatedDateFromToken(token)
        return (username == user.username
                && !isTokenExpired(token)
                && !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()))
    }

    private fun getCreatedDateFromToken(token: String): Date? {
        val claims = getClaimsFromToken(token)
        return Date(claims[CLAIM_KEY_CREATED] as Long)
    }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val expiration = getExpirationDateFromToken(token)
            expiration!!.before(Date())
        } catch (e: Exception) {
            true
        }
    }

    private fun isCreatedBeforeLastPasswordReset(created: Date?, lastPasswordReset: Date?): Boolean {
        return lastPasswordReset != null && created != null && created.before(lastPasswordReset)
    }

    private fun getExpirationDateFromToken(token: String): Date? {
        val claims = getClaimsFromToken(token)
        return claims.expiration
    }

    fun refreshAccessToken(refreshToken: String?): String? {
        val userNameRefreshToken = getUsernameFromToken(refreshToken) ?: return null
        return createJWT(userNameRefreshToken, userNameRefreshToken, ACCESS_TOKEN_TTL)
    }

    fun getUsernameFromToken(token: String?): String? {
        return if (token == null) {
            null
        } else {
            val claims = getClaimsFromToken(token)
            return claims.subject
        }
    }

    private fun getClaimsFromToken(token: String): Claims {
        return decodeJWT(token)
    }

    @Scheduled(cron = "0 0 0 ? * * *")
    fun invalidateRefreshToken() {
        refreshRefreshTokenIfNeeded()
    }
}