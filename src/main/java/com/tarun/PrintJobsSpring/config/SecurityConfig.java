package com.tarun.PrintJobsSpring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.tarun.PrintJobsSpring.security.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService) {

        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .userDetailsService(userDetailsService)

            .authorizeHttpRequests(auth -> auth

                // Public pages and static resources
                .requestMatchers(
                        "/",
                        "/index.html",
                        "/customer/**",
                        "/display/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico"
                ).permitAll()

                // Public customer APIs
                .requestMatchers(
                        HttpMethod.POST,
                        "/printjobs",
                        "/printjobs/upload"
                ).permitAll()

                .requestMatchers(
                        HttpMethod.GET,
                        "/printjobs/track/**",
                        "/printjobs/current",
                        "/printjobs/{id:[0-9]+}"
                ).permitAll()

                // Public login page
                .requestMatchers(
                        "/admin/admin-login.html",
                        "/login"
                ).permitAll()

                // Swagger
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                ).permitAll()

                // Protected admin pages and APIs
                .requestMatchers(
                        "/admin/**",
                        "/printjobs/**"
                ).hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/admin/admin-login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl(
                        "/admin/dashboard.html",
                        true
                )
                .failureUrl(
                        "/admin/admin-login.html?error=true"
                )
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl(
                        "/admin/admin-login.html?logout=true"
                )
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}