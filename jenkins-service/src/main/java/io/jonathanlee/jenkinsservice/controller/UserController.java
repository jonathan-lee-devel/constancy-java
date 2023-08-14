package io.jonathanlee.jenkinsservice.controller;

import io.jonathanlee.commonlib.principal.PrincipalHelper;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping("/")
public class UserController {

  @Value("${constancy.front-end.url}")
  private String frontEndUrl;

  @GetMapping
  public RedirectView loginRedirect(@RequestParam(value = "continue", required = false) String shouldContinue) {
    return new RedirectView((shouldContinue != null) ? frontEndUrl + "/keycloak-login-success?continue" : frontEndUrl + "/keycloak-login-success");
  }

  @GetMapping("/jenkins")
  public RedirectView jenkinsServiceLoginRedirect(@RequestParam(value = "continue", required = false) String shouldContinue) {
    return new RedirectView((shouldContinue != null) ? frontEndUrl + "/keycloak-login-success?continue" : frontEndUrl + "/keycloak-login-success");
  }

  @GetMapping(
      value = "/profile",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Map<String, String>> getProfile(Principal principal) {
    Map<String, String> profile = new HashMap<>();
    profile.put("email", PrincipalHelper.extractUsername(principal));
    profile.put("firstName", PrincipalHelper.extractFirstName(principal));
    profile.put("lastName", PrincipalHelper.extractLastName(principal));

    return ResponseEntity.ok(profile);
  }

}

