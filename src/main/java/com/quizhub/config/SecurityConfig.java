package com.quizhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/api/**"),
                    new AntPathRequestMatcher("/topics/api/**")
                )
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico",
                    "/h2-console/**",
                    "/login",
                    "/error"
                ).permitAll()
                // Admin specific routes
                .requestMatchers(
                    "/questions/**",
                    "/topics/new",
                    "/topics/*/edit",
                    "/topics/*/delete",
                    "/topics/api/**",
                    "/quizzes/create",
                    "/quizzes/*/edit",
                    "/quizzes/*/delete",
                    "/quizzes/*/status",
                    "/api/import/**"
                ).hasRole("ADMIN")
                // General authenticated routes
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
