package com.lexisnexis.batch.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .formLogin().disable()
            .anonymous().disable()
            .authorizeRequests().anyRequest().authenticated();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userService)
            .passwordEncoder(new BCryptPasswordEncoder());

//        auth.inMemoryAuthentication()
//                .withUser("rbbuser1").password("{noop}password").authorities("ROLE_USER")
//                .and()
//                .withUser("rbbuser2").password("{noop}password").authorities("ROLE_API_USER", "ROLE_USER");
    }

    @Override
    @Bean("customAuthManager")
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
