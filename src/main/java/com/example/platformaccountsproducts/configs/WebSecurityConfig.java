package com.example.platformaccountsproducts.configs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.platformaccountsproducts.services.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    @Lazy
        private AccountService accountService;
            
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
    .authorizeHttpRequests(auth -> auth
    .requestMatchers("/myAccount/**").authenticated()
        .anyRequest().permitAll()
    )
    .formLogin(form -> form
        .loginPage("/login")
        .defaultSuccessUrl("/myAccount", true)
        .failureUrl("/login?error=true")
        .permitAll()
    )
       .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .clearAuthentication(true) // Очищення аутентифікації
            .deleteCookies("JSESSIONID", "remember-me") // Видалення cookies
            .permitAll()
        );
return http.build();
    }
}

