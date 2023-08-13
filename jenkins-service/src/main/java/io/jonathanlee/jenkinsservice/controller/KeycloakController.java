package io.jonathanlee.jenkinsservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/realms/constancy/login-actions")
public class KeycloakController {

  @Value("${constancy.front-end-url}")
  private String frontEndUrl;

  @GetMapping("/login")
  public RedirectView getLogin() {
    return new RedirectView(frontEndUrl);
  }

  @GetMapping("/registration")
  public RedirectView getRegistration() {
    return new RedirectView(frontEndUrl);
  }

}
