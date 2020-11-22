package com.github.potjerodekool.jwtserver.config

import com.github.potjerodekool.jwtserver.jwt.JWTAuthenticationFilter
import com.github.potjerodekool.jwtserver.jwt.JwtAuthenticationEntryPoint
import com.github.potjerodekool.jwtserver.jwt.JwtService
import com.github.potjerodekool.jwtserver.service.AccountService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
class WebSecurity(private val jwtService: JwtService,
                  private val accountService: AccountService,
                  private var unauthorizedHandler : JwtAuthenticationEntryPoint,
                  @Value("\${server.servlet.context-path}") private val contextPath: String): WebSecurityConfigurerAdapter() {

    @Bean
    @Throws(Exception::class)
    fun customAuthenticationManager(): AuthenticationManager {
        return authenticationManager()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationTokenFilterBean(): JWTAuthenticationFilter {
        return JWTAuthenticationFilter(jwtService, accountService)
    }

    override fun configure(web: WebSecurity?) {
        web?.ignoring()?.antMatchers("/**")
    }

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()

                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

                // don't create session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()
                .antMatchers("$contextPath/authenticate").permitAll()
                .antMatchers("$contextPath/auth/**").permitAll()
                .antMatchers("$contextPath/signup").permitAll()
                .antMatchers("$contextPath/signup/**").permitAll()
                .antMatchers(HttpMethod.GET, "$contextPath/swagger-ui.html").permitAll()
                .antMatchers(HttpMethod.GET, "$contextPath/webjars/springfox-swagger-ui/**").permitAll()
                .antMatchers(HttpMethod.GET, "$contextPath/swagger-resources/**").permitAll()
                .antMatchers(HttpMethod.GET, "$contextPath/v2/api-docs").permitAll()
                .antMatchers(HttpMethod.GET, "$contextPath/actuator").permitAll()
                .antMatchers(HttpMethod.GET, "$contextPath/health").permitAll()
                .antMatchers(HttpMethod.GET, "$contextPath/metrics").permitAll()

                .antMatchers(HttpMethod.OPTIONS, "$contextPath/**").permitAll()
                .anyRequest().authenticated()

        // Custom JWT based security filter
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter::class.java)

        // disable page caching
        httpSecurity.headers().cacheControl()
    }

}