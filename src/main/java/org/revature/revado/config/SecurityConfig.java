package org.revature.revado.config;

import org.revature.revado.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration   // Marks this as Spring configuration class
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // Constructor injection
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }
    // PASSWORD ENCODER-Used to hash user passwords before saving to DB and BCrypt is secure algorithm
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS CONFIGURATION-Allows Angular (port 4200) to call backend (port 8080)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // Allow frontend origin
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Allow HTTP methods
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        // Allow headers like Authorization, Content-Type
        configuration.setAllowedHeaders(List.of("*"));

        // Allow credentials (if needed for JWT / cookies)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        // Apply CORS settings to all endpoints
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // SECURITY FILTER CHAIN-Defines: Which endpoints are public,Which require authentication, JWT filter configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Enable CORS
                .cors(cors -> {})

                // Disable CSRF (not needed for REST APIs with JWT)
                .csrf(csrf -> csrf.disable())

                // Make app stateless (no session storage)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // Public endpoints (no token required)
                        .requestMatchers("/register", "/login").permitAll()

                        // All API endpoints require JWT authentication
                        .requestMatchers("/api/**").authenticated()

                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                );

        // Add custom JWT filter before Spring's authentication filter
        http.addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}