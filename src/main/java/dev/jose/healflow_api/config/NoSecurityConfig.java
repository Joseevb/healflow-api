package dev.jose.healflow_api.config;

import dev.jose.healflow_api.persistence.entities.UserEntity;
import dev.jose.healflow_api.persistence.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Profile("no-sec")
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class NoSecurityConfig {

  private final UserRepository userRepository;

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService inMemoryUserDetailsService(PasswordEncoder passwordEncoder) {
    var devUser =
        User.builder()
            .username("dev@healflow.dev")
            .password(passwordEncoder.encode("dev"))
            .roles("USER")
            .build();

    return new InMemoryUserDetailsManager(devUser);
  }

  @Bean
  @Primary
  SecurityFilterChain noSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.cors(AbstractHttpConfigurer::disable)
        .headers(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .httpBasic(Customizer.withDefaults())
        .addFilterAfter(
            new DevUserIdFilter(userRepository), UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Slf4j
  @RequiredArgsConstructor
  static class DevUserIdFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

      // Fetch the first available user from the database and set their authId as userId
      UserEntity user =
          userRepository
              .findByEmail("john.doe@email.com")
              .orElseGet(() -> userRepository.findAll().stream().findFirst().orElse(null));

      if (user != null) {
        request.setAttribute("userId", user.getAuthId());
        log.debug(
            "Dev mode: Set userId to {} (user: {} {})",
            user.getAuthId(),
            user.getFirstName(),
            user.getLastName());
      } else {
        log.warn("Dev mode: No users found in database. userId attribute not set.");
      }

      filterChain.doFilter(request, response);
    }
  }
}
