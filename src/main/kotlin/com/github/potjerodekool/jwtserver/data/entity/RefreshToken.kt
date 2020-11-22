package com.github.potjerodekool.jwtserver.data.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "refreshtoken")
@Entity
data class RefreshToken(
        @Id
        @Column(name = "id", nullable = false)
        var id: Int = 0,
        @Column(name = "token", nullable = false)
        var token: String) {
}