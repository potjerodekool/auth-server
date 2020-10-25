package com.github.potjerodekool.jwtserver.data.entity

import javax.persistence.*

@Table(name = "user")
@Entity
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        var id: Int? = null,
        @Column(name = "userid")
        var userid: String,
        @Column(name = "password")
        var password: String,
        @Column(name = "resettoken")
        var resettoken: String? = null) {

    constructor(): this(id = 0, userid = "", password = "")
}