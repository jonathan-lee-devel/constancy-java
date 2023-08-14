package io.jonathanlee.constancygateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

@Configuration
@Profile("local")
@RequiredArgsConstructor
public class SecurityLocalConfig {

  private final ReactiveClientRegistrationRepository clientRegistrationRepository;

  @Value("${constancy.gateway.url}")
  private String gatewayUrl;

  @Value("${constancy.front-end.url}")
  private String frontEndUrl;

  @Bean
  protected ServerLogoutSuccessHandler logoutSuccessHandler() {
    OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
    logoutSuccessHandler.setPostLogoutRedirectUri(gatewayUrl);
    return logoutSuccessHandler;
  }

  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    http
        .csrf(CsrfSpec::disable)
        .authorizeExchange(authorizeExchangeSpec -> {
          authorizeExchangeSpec.pathMatchers("/logout", "/actuator/**").permitAll();
          authorizeExchangeSpec.anyExchange().authenticated();
        })
        .oauth2Login(oAuth2LoginSpec -> {
          oAuth2LoginSpec.clientRegistrationRepository(clientRegistrationRepository);
          oAuth2LoginSpec.authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler(frontEndUrl));
        })
        .logout(logoutSpec -> {
          logoutSpec.logoutUrl("/logout");
          logoutSpec.logoutSuccessHandler(logoutSuccessHandler());
        });

    return http.build();
  }

}
