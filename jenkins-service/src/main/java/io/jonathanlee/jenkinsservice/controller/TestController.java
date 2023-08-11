package io.jonathanlee.jenkinsservice.controller;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TestController {

  private final WebClient jenkinsWebClient;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getMapping(Principal principal) {
    log.info("Principal::name -> Subject -> User ID: {}", principal.getName());
    log.info("Token attributes: {}", ((JwtAuthenticationToken)principal).getTokenAttributes());
    log.info("Token attributes (given_name): {}", ((JwtAuthenticationToken)principal).getTokenAttributes().getOrDefault("given_name", "undefined"));
    log.info("Token attributes (family_name): {}", ((JwtAuthenticationToken)principal).getTokenAttributes().getOrDefault("family_name", "undefined"));
    UriSpec<RequestBodySpec> uriSpec = jenkinsWebClient.method(HttpMethod.GET);
    RequestBodySpec requestBodySpec = uriSpec
        .uri("http://localhost:8081/job/TestJob/api/json")
        .header(
            "Authorization",
            "Basic " + Base64.getEncoder().encodeToString(("jonathan" + ":" + "11d685921f976137027eca760257193142").getBytes(StandardCharsets.UTF_8))
        );
    Mono<String> response = requestBodySpec.exchangeToMono(clientResponse -> {
      if (clientResponse.statusCode().is2xxSuccessful()) {
        return clientResponse.bodyToMono(String.class);
      }
      return clientResponse.createException()
          .flatMap(Mono::error);
    });

    String jsonResponse = "{\"error\":\"error\"}";
    try {
      jsonResponse = response.block();
    } catch (WebClientResponseException webClientResponseException) {
      log.error("Error: {}", webClientResponseException.getMessage());
    }

    return ResponseEntity.ok(jsonResponse);
  }

}
