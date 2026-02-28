package dev.jose.healflow_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to extract user ID and role from JWT token and add them to the request context and
 * security context.
 *
 * <p>Extracts:
 * - User ID from 'sub' claim
 * - Specialist ID from 'specialist_id' claim (if present)
 * - Role from 'role' claim (converted to Spring ROLE_ format)
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth instanceof JwtAuthenticationToken jwtAuth) {
      Jwt jwt = jwtAuth.getToken();

      // Extract user ID
      String userIdStr = jwt.getClaimAsString("sub");
      if (userIdStr != null) {
        request.setAttribute("userId", UUID.fromString(userIdStr));
      }

      // Extract specialist ID if present
      String specialistIdStr = jwt.getClaimAsString("specialist_id");
      if (specialistIdStr != null) {
        request.setAttribute("specialistId", UUID.fromString(specialistIdStr));
      }

      // Extract role and create new authentication with authorities
      String role = jwt.getClaimAsString("role");
      if (role != null) {
        // Convert lowercase role to Spring format (e.g., "admin" -> "ROLE_ADMIN")
        String springRole = "ROLE_" + role.toUpperCase();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(springRole));

        // Create new JwtAuthenticationToken with authorities
        JwtAuthenticationToken newAuth =
            new JwtAuthenticationToken(jwt, authorities, jwtAuth.getName());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
      }
    }

    filterChain.doFilter(request, response);
  }
}
