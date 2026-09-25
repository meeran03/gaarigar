package com.gianteyes.gaarigar.security;

import com.gianteyes.gaarigar.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
@Configuration
public class SecurityConfig {
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean @Order(1)
    SecurityFilterChain api(HttpSecurity http, UserService users, @Value("${jwt.secret}") String secret) throws Exception {
        if (secret.length() < 32) throw new IllegalStateException("JWT_SECRET must contain at least 32 characters");
        http.securityMatcher("/api/**").csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/api/auth/register/admin").hasAuthority("ADMIN")
                .requestMatchers("/api/auth/login", "/api/auth/register/customer", "/api/auth/register/mechanic", "/api/auth/register/petrolpump").permitAll()
                .requestMatchers("/api/sms/**").denyAll()
                .requestMatchers(HttpMethod.POST, "/api/category/**", "/api/standard-service/**").hasAuthority("ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(new CustomAuthenticationFilter(secret, users), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean @Order(2)
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(a -> a
                .requestMatchers("/", "/index.html", "/images/**", "/css/**", "/js/**", "/admin/login", "/actuator/health", "/error", "/chat", "/chat/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority("ADMIN", "DEMO_ADMIN")
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().denyAll())
            .formLogin(f -> f.loginPage("/admin/login").usernameParameter("phone").passwordParameter("password")
                .defaultSuccessUrl("/admin/reports/services", true).failureUrl("/admin/login?error").permitAll())
            .logout(l -> l.logoutUrl("/admin/logout").logoutSuccessUrl("/admin/login?logout"))
            .headers(h -> h.contentSecurityPolicy(c -> c.policyDirectives("frame-ancestors 'none'; object-src 'none'; base-uri 'self'")));
        return http.build();
    }
}
