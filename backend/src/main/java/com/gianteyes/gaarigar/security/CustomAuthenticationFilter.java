package com.gianteyes.gaarigar.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.gianteyes.gaarigar.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final String secret;
    private final UserService users;
    public CustomAuthenticationFilter(String secret, UserService users) { this.secret = secret; this.users = users; }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null) {
            try {
                if (!header.startsWith("Bearer ")) throw new IllegalArgumentException();
                var token = JWT.require(Algorithm.HMAC256(secret)).withIssuer("auth0").build().verify(header.substring(7));
                var user = users.getUserByPhone(token.getSubject()).orElseThrow();
                if (!Boolean.TRUE.equals(user.getIsActive()) || token.getClaim("role").asString() == null || token.getExpiresAt() == null) throw new IllegalArgumentException();
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getPhone(), null,
                    List.of(new SimpleGrantedAuthority(user.getUserType().name()))));
            } catch (Exception e) {
                SecurityContextHolder.clearContext(); response.sendError(401, "Invalid or expired access token"); return;
            }
        }
        chain.doFilter(request, response);
    }
}
