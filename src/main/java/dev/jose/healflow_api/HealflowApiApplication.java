package dev.jose.healflow_api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@Slf4j
@EnableRetry
@SpringBootApplication
public class HealflowApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(HealflowApiApplication.class, args);
  }
}
