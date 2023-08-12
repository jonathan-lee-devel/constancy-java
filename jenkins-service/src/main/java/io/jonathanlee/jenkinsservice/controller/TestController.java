package io.jonathanlee.jenkinsservice.controller;

import io.jonathanlee.commonlib.auth.BasicAuthHelper;
import io.jonathanlee.commonlib.constants.JsonResponses;
import io.jonathanlee.commonlib.principal.PrincipalHelper;
import io.jonathanlee.jenkinsservice.dto.JenkinsRequestDto;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    log.info("Principal::name -> Subject -> User ID: {}", PrincipalHelper.extractUsername(principal));
    log.info("Token attributes (given_name): {}", PrincipalHelper.extractFirstName(principal));
    log.info("Token attributes (family_name): {}", PrincipalHelper.extractLastName(principal));
    UriSpec<RequestBodySpec> uriSpec = jenkinsWebClient.method(HttpMethod.GET);
    RequestBodySpec requestBodySpec = uriSpec
        .uri("http://localhost:8081/job/TestJob/api/json")
        .header(BasicAuthHelper.AUTHORIZATION_HEADER, BasicAuthHelper.getAuthorizationHeaderValue("jonathan", "11d685921f976137027eca760257193142"));

    return ResponseEntity.ok(getResponse(requestBodySpec));
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<String> postMapping(Principal principal, @Validated @RequestBody JenkinsRequestDto jenkinsRequestDto) {
    String fullUrl = jenkinsRequestDto.getHost() + "/" + jenkinsRequestDto.getUrlEnd() + "/api/json";
    log.info("User with ID: {} making request to: URL: {}", PrincipalHelper.extractUsername(principal), fullUrl);
    UriSpec<RequestBodySpec> uriSpec = jenkinsWebClient.method(HttpMethod.GET);
    RequestBodySpec requestBodySpec = uriSpec
        .uri(fullUrl)
        .header(BasicAuthHelper.AUTHORIZATION_HEADER, BasicAuthHelper.getAuthorizationHeaderValue(jenkinsRequestDto.getUsername(), jenkinsRequestDto.getToken()));

    return ResponseEntity.ok(getResponse(requestBodySpec));
  }

  private String getResponse(RequestBodySpec requestBodySpec) {
    Mono<String> response = requestBodySpec.exchangeToMono(clientResponse -> {
      if (clientResponse.statusCode().is2xxSuccessful()) {
        return clientResponse.bodyToMono(String.class);
      }
      return clientResponse.createException()
          .flatMap(Mono::error);
    });

    String jsonResponse = JsonResponses.DEFAULT_ERROR_RESPONSE;
    try {
      jsonResponse = response.block();
    } catch (WebClientResponseException webClientResponseException) {
      log.error("Error: {}", webClientResponseException.getMessage());
    }

    return jsonResponse;
  }

}
