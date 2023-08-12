package io.jonathanlee.commonlib.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthHelper {

  private BasicAuthHelper() {}

  public static final String AUTHORIZATION_HEADER = "Authorization";

  public static String getAuthorizationHeaderValue(String username, String password) {
    return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
  }

}
