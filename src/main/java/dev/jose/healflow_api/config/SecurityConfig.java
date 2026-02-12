package dev.jose.healflow_api.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Profile("!no-sec & !dev")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private static final String allowedOrigins = "localhost:3000";

  private static final String[] PUBLIC_RESOURCES = {
    "/v3/api-docs/**", "/actuator/**", "/docs/**", "/h2-console"
  };

  private static final String[] GET_ALLOWED_RESOURCES = {
    "/specialists/types",
  };

  @Bean
  SecurityFilterChain filterChain(
      HttpSecurity http,
      @Value("${api.allowedOrigins}") String allowedOrigins,
      ApiKeyAuthenticationFilter apiKeyFilter)
      throws Exception {
    return http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .headers(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(PUBLIC_RESOURCES)
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        Arrays.asList(GET_ALLOWED_RESOURCES).stream()
                            .map("/api/v1"::concat)
                            .toArray(String[]::new))
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
        .addFilterBefore(apiKeyFilter, BearerTokenAuthenticationFilter.class)
        .addFilterAfter(new JwtAuthenticationFilter(), BearerTokenAuthenticationFilter.class)
        .build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin(allowedOrigins);
    configuration.addAllowedMethod(HttpMethod.GET);
    configuration.addAllowedMethod(HttpMethod.POST);
    configuration.addAllowedMethod(HttpMethod.PUT);
    configuration.addAllowedMethod(HttpMethod.DELETE);
    configuration.addAllowedMethod(HttpMethod.PATCH);
    configuration.addAllowedMethod(HttpMethod.OPTIONS);
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
