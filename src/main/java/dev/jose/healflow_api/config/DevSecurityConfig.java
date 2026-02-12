package dev.jose.healflow_api.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;
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

@Profile("dev $ !no-sec")
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
public class DevSecurityConfig {
  @Value("${api.allowedOrigins}")
  private String allowedOrigins;

  private static final String[] PUBLIC_RESOURCES = {
    "/v3/api-docs/**", "/actuator/**", "/docs/**", "/h2-console/**"
  };

  private static final String[] GET_ALLOWED_RESOURCES = {
    "/specialists/types",
  };

  @Bean
  SecurityFilterChain devFilterChain(
      HttpSecurity http,
      @Value("${api.allowedOrigins}") String allowedOrigins,
      ApiKeyAuthenticationFilter apiKeyFilter)
      throws Exception {
    return http.cors(cors -> cors.configurationSource(devCorsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(withDefaults()).disable())
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(PUBLIC_RESOURCES)
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        Arrays.stream(GET_ALLOWED_RESOURCES)
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
  CorsConfigurationSource devCorsConfigurationSource() {
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
