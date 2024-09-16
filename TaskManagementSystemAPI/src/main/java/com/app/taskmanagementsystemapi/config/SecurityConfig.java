package com.app.taskmanagementsystemapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .headers(headers -> headers.cacheControl(HeadersConfigurer.CacheControlConfig::disable))
                .httpBasic(Customizer.withDefaults())
//                .userDetailsService(userDetailsService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,"/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/users/current-user").hasAnyAuthority("ADMIN", "PROJECT_MANAGER", "DEVELOPER")
                        .requestMatchers(HttpMethod.GET, "/api/users/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/manager/**").hasAuthority("PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/users/manager/**").hasAuthority("PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/manager/**").hasAuthority("PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/users/manager/**").hasAuthority("PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/projects/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/projects/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/projects/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/tasks/manager/**").hasAuthority("PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/tasks/manager/**").hasAuthority("PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/manager/**").hasAuthority("PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/manager/**").hasAuthority("PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/developer/**").hasAuthority("DEVELOPER")
                        .requestMatchers(HttpMethod.GET, "/api/tasks/developer/**").hasAuthority("DEVELOPER")

                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()

                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter , UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

