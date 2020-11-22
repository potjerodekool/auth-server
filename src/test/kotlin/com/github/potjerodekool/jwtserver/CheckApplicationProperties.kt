package com.github.potjerodekool.jwtserver

import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

class CheckApplicationProperties {

    @Test
    fun test() {
        val properties = Properties()
        properties.load(File("src/main/resources/application.properties").inputStream())
        assertEquals("user@server.com", properties["spring.mail.username"])
        assertEquals("secret", properties["spring.mail.password"])

        println(UUID.randomUUID().toString())
        val uid = "ff843c7a-1066-4507-b999-e0defd5fa6e5\n"

        val base64 = String(Base64.getEncoder().encode(uid.toByteArray()))
        println(base64)
    }
}