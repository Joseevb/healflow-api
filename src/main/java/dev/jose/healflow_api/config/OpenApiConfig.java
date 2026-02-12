package dev.jose.healflow_api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  ModelResolver modelResolver(ObjectMapper mapper) {
    return new ModelResolver(mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE));
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .components(new io.swagger.v3.oas.models.Components()
            .addSecuritySchemes("API Key", new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-API-KEY")
                .description("API Key for authentication")));
  }
}
