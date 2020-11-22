package com.github.potjerodekool.integrator.config

import io.swagger.annotations.Api
import io.swagger.models.auth.In
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiKey
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SwaggerConfig {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(listOf(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api::class.java))
                .paths(PathSelectors.any())
                .build()

    }

    private fun apiKey(): ApiKey? = ApiKey(HttpHeaders.AUTHORIZATION, HttpHeaders.AUTHORIZATION, In.HEADER.name)
}
