package io.jonathanlee.jenkinsservice.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class KeycloakLogoutHandler implements LogoutHandler {

  private final RestTemplate restTemplate = new RestTemplate();

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    logoutFromKeycloak((OidcUser) authentication.getPrincipal());
  }

  private void logoutFromKeycloak(OidcUser oidcUser) {
    String endSessionEndpoint = oidcUser.getIssuer() + "/protocol/openid-connect/logout";
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
        .fromUriString(endSessionEndpoint)
        .queryParam("id_token_hint", oidcUser.getIdToken().getTokenValue());

    ResponseEntity<String> logoutResponse = restTemplate.getForEntity(uriComponentsBuilder.toUriString(), String.class);
    if (logoutResponse.getStatusCode().is2xxSuccessful()) {
      log.info("Successfully logged out from Keycloak");
    } else {
      log.error("Unable to log out from Keycloak");
    }
  }

}
