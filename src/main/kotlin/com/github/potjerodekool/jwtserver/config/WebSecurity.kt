package com.github.potjerodekool.jwtserver.config

import com.github.potjerodekool.jwtserver.jwt.JWTAuthenticationFilter
import com.github.potjerodekool.jwtserver.jwt.JwtAuthenticationEntryPoint
import com.github.potjerodekool.jwtserver.jwt.JwtService
import com.github.potjerodekool.jwtserver.service.AccountService
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
                  var unauthorizedHandler : JwtAuthenticationEntryPoint): WebSecurityConfigurerAdapter() {

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
        super.configure(web)
        web?.ignoring()?.antMatchers("/**");
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
                .antMatchers("/auth").permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/signup").permitAll()
                .antMatchers("/signup/**").permitAll()
                .antMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
                .antMatchers(HttpMethod.GET, "/webjars/springfox-swagger-ui/**").permitAll()
                .antMatchers(HttpMethod.GET, "/swagger-resources/**").permitAll()
                .antMatchers(HttpMethod.GET, "/v2/api-docs").permitAll()
                .antMatchers(HttpMethod.GET, "/actuator").permitAll()
                .antMatchers(HttpMethod.GET, "/health").permitAll()
                .antMatchers(HttpMethod.GET, "/metrics").permitAll()

                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()

        // Custom JWT based security filter
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter::class.java)

        // disable page caching
        httpSecurity.headers().cacheControl()
    }

}