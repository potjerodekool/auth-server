package com.github.potjerodekool.jwtserver.data.entity

import java.time.LocalDateTime
import javax.persistence.*

@Table(name = "user")
@Entity
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_id", nullable = false)
        var id: Int? = null,
        @Column(name = "uuid", nullable = false)
        var uuid: String,
        @Column(name = "email", nullable = false)
        var email: String,
        @Column(name = "password", nullable = false)
        var password: String,
        @Column(name = "resettoken")
        var resetToken: String? = null,
        @Column(name = "resetoken_ttl")
        var resetTokenTtl: LocalDateTime? = null) {

}