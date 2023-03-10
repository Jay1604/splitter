package de.hhu.propra.splitter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain configure(HttpSecurity chainBuilder) throws Exception {
    chainBuilder
        .csrf()
        .ignoringAntMatchers("/api/**")
        .and()
        .authorizeHttpRequests(
            configurer -> configurer
                .antMatchers(
                    "/public",
                    "/css/*",
                    "/favicon.ico",
                    "/api/**"
                )
                .permitAll()
                .anyRequest()
                .authenticated()

        )
        .oauth2Login(config ->
            config.userInfoEndpoint(
                info -> info.userService(new AppUserService())
            ));

    return chainBuilder.build();
  }

}