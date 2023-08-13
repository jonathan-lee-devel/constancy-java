package io.jonathanlee.jenkinsservice.config;

import io.jonathanlee.jenkinsservice.exception.Oauth2LoginConfigurationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Profile("local")
@RequiredArgsConstructor
public class SecurityLocalConfig {

  @Value("${constancy.jenkins.service.url}")
  private String jenkinsServiceUrl;

  @Value("${constancy.front-end-url}")
  private String frontEndUrl;

  private final ClientRegistrationRepository clientRegistrationRepository;

  @Bean
  protected CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(List.of(frontEndUrl));
    corsConfiguration.setAllowedMethods(List.of("*"));
    corsConfiguration.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
    return urlBasedCorsConfigurationSource;
  }

  @Bean
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
  }

  @Bean
  protected LogoutSuccessHandler oidcLogoutSuccessHandler() {
    OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
    successHandler.setPostLogoutRedirectUri(jenkinsServiceUrl);
    return successHandler;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry.anyRequest().authenticated())
        .logout(httpSecurityLogoutConfigurer -> {
          httpSecurityLogoutConfigurer.init(http);
          httpSecurityLogoutConfigurer.logoutUrl("/logout");
          httpSecurityLogoutConfigurer.logoutSuccessHandler(oidcLogoutSuccessHandler());
          httpSecurityLogoutConfigurer.deleteCookies("JSESSIONID");
          httpSecurityLogoutConfigurer.clearAuthentication(true);
        });

    http.oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer.jwt(jwtConfigurer -> {}));

    http.oauth2Login(httpSecurityOAuth2LoginConfigurer -> {
      try {
        httpSecurityOAuth2LoginConfigurer.init(http);
      } catch (Exception exception) {
        throw new Oauth2LoginConfigurationException(exception);
      }
    });

    return http.build();
  }

}
