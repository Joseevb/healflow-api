package dev.jose.healflow_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${api.prefix:/api}")
  private String prefix;

  @Value("${api.version:/v1}")
  private String version;

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    configurer.addPathPrefix(basePath(), this::isApiController);
  }

  private boolean isApiController(Class<?> controllerClass) {
    return controllerClass.isAnnotationPresent(RestController.class)
        && controllerClass.getPackageName().startsWith("dev.jose.healflow_api");
  }

  @Bean
  String basePath() {
    return prefix + "/" + version;
  }
}
