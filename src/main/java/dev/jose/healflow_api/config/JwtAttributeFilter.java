package dev.jose.healflow_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

record JwtContext(UUID userId, UUID specialistId, List<SimpleGrantedAuthority> authorities) {}

@Component
public class JwtAttributeFilter extends OncePerRequestFilter {

  private final Function<String, Integer> toInteger = str -> Integer.valueOf(str);

  private final Function<Jwt, JwtContext> extractContext =
      jwt ->
          new JwtContext(
              UUID.fromString(jwt.getSubject()),
              Optional.ofNullable(jwt.getClaimAsString("specialist_id"))
                  .map(UUID::fromString)
                  .orElse(null),
              Optional.ofNullable(jwt.getClaimAsString("role"))
                  .map(r -> List.of(new SimpleGrantedAuthority("ROLE_" + r.toUpperCase())))
                  .orElseGet(List::of));

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    extractJwtContext()
        .ifPresent(
            ctx -> {
              req.setAttribute("userId", ctx.userId());
              Optional.ofNullable(ctx.specialistId())
                  .ifPresent(id -> req.setAttribute("specialistId", id));
              rebuildAuthentication(ctx);
            });

    chain.doFilter(req, res);
  }

  private Optional<JwtContext> extractJwtContext() {
    return Optional.of(SecurityContextHolder.getContext())
        .map(SecurityContext::getAuthentication)
        .filter(JwtAuthenticationToken.class::isInstance)
        .map(JwtAuthenticationToken.class::cast)
        .map(this::toContext);
  }

  private JwtContext toContext(JwtAuthenticationToken auth) {
    Jwt jwt = auth.getToken();

    this.extractContext.apply(jwt);

    return new JwtContext(
        UUID.fromString(jwt.getSubject()),
        Optional.ofNullable(jwt.getClaimAsString("specialist_id"))
            .map(UUID::fromString)
            .orElse(null),
        Optional.ofNullable(jwt.getClaimAsString("role"))
            .map(r -> List.of(new SimpleGrantedAuthority("ROLE_" + r.toUpperCase())))
            .orElseGet(List::of));
  }

  private void rebuildAuthentication(JwtContext ctx) {
    extractJwtContext()
        .map(JwtContext::authorities)
        .flatMap(
            auths ->
                Optional.of(SecurityContextHolder.getContext())
                    .map(SecurityContext::getAuthentication)
                    .filter(JwtAuthenticationToken.class::isInstance)
                    .map(JwtAuthenticationToken.class::cast)
                    .map(
                        auth -> new JwtAuthenticationToken(auth.getToken(), auths, auth.getName())))
        .ifPresent(SecurityContextHolder.getContext()::setAuthentication);
  }
}
