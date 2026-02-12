package dev.jose.healflow_api.config;

import dev.jose.medicines.api.MedicinesApi;
import dev.jose.medicines.api.StatisticsApi;
import dev.jose.medicines.client.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MedicinesClientConfig {

  @Bean
  ApiClient medicinesApiClient(@Value("${medicines-api-url}") String medicinesApiUrl) {
    ApiClient client = new ApiClient();
    client.setBasePath(medicinesApiUrl);
    return client;
  }

  @Bean
  MedicinesApi medicinesApi(ApiClient medicinesApiClient) {
    return new MedicinesApi(medicinesApiClient);
  }

  @Bean
  ApiClient medicinesStatisticsApiClient(@Value("${medicines-api-url}") String medicinesApiUrl) {
    ApiClient client = new ApiClient();
    client.setBasePath(medicinesApiUrl);
    return client;
  }

  @Bean
  StatisticsApi medicinesStatisticsApi(ApiClient medicinesStatisticsApiClient) {
    return new StatisticsApi(medicinesStatisticsApiClient);
  }
}
