package dev.jose.healflow_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  @Value("${api.key-header:X-API-KEY}")
  private String HEADER_NAME;

  @Value("${api.key}")
  private String validApiKey;

  @Value("${api.key-path}")
  private String apiKeyAllowedPath;

  @Value("${api.prefix:/api}")
  private String prefix;

  @Value("${api.version:v1}")
  private String version;

  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  private String getFullBasePath() {
    return prefix + "/" + version;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    String[] paths = apiKeyAllowedPath.split(",");
    String basePath = getFullBasePath();

    return Arrays.stream(paths)
        .map(String::trim)
        .noneMatch(p -> pathMatcher.match(basePath + p, path));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String key = request.getHeader(HEADER_NAME);
    if (key == null || !key.equals(validApiKey)) {
      filterChain.doFilter(request, response);
      return;
    }

    Authentication auth =
        new UsernamePasswordAuthenticationToken(
            "apiKeyUser", key, List.of(new SimpleGrantedAuthority("ROLE_API_KEY_USER")));
    SecurityContextHolder.getContext().setAuthentication(auth);

    filterChain.doFilter(request, response);
  }
}
