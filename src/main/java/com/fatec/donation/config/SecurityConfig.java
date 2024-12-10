package com.fatec.donation.config;

import com.fatec.donation.jwt.JwtService;
import com.fatec.donation.jwt.filter.JwtFilter;
import com.fatec.donation.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public JwtFilter jwtFilter(JwtService jwtService, UserService userService){
        return new JwtFilter(jwtService, userService);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build());
        return manager;
    }
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtFilter jwtFilter) throws Exception {
        return httpSecurity
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(httpSecurity))
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.POST,"/api/v1/user/auth").anonymous();
                    auth.requestMatchers(HttpMethod.POST,"/api/v1/user/logout").authenticated();
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/user/register").permitAll();
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/user/update/{userId}").authenticated();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/user/profile").authenticated();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/user/check-access").authenticated();
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/user/complete-register").authenticated();

//                    auth.requestMatchers(HttpMethod.GET, "/api/v1/images/users/{filename}").authenticated();
//                    auth.requestMatchers(HttpMethod.GET, "/api/v1/images/groups/{filename}").authenticated();
//                    auth.requestMatchers(HttpMethod.GET, "/api/v1/images/users/images").hasAnyRole("ADMIN", "USER");

                    auth.requestMatchers(HttpMethod.POST, "/api/v1/groups").hasAnyRole("ADMIN", "USER");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/groups/{groupName}").hasAnyRole("ADMIN", "USER");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/v1/groups/{groupName}").hasAnyRole("ADMIN", "USER");
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/groups/profile/{groupName}").authenticated();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/groups/authorities").authenticated();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/groups/search").authenticated();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/groups/search/member").authenticated();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/groups/search/owner").authenticated();
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/groups/join/{groupName}").hasAnyRole("ADMIN", "USER");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/groups/block/{groupName}/{userId}").hasAnyRole("ADMIN", "USER");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/groups/unblock/{groupName}/{userId}").hasAnyRole("ADMIN", "USER");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/groups/join/{requestId}/accept").hasAnyRole("ADMIN", "USER");
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/groups/join/{requestId}/reject").hasAnyRole("ADMIN", "USER");

                    auth.anyRequest().permitAll();
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000", "http://127.0.0.1:8888", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Requestor-Type", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("X-Get-Header"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
