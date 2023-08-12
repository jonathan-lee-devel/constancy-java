package io.jonathanlee.commonlib.principal;

import io.jonathanlee.commonlib.constants.UserAttributes;
import java.security.Principal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class PrincipalHelper {

  private static final String GIVEN_NAME = "given_name";

  private static final String FAMILY_NAME = "family_name";

  private static final String UNRECOGNIZED_PRINCIPAL_METHOD_ERROR_MESSAGE = "Principal not instance of JwtAuthenticationToken or OAuth2AuthenticationToken";

  private PrincipalHelper() {}

  public static String extractUsername(Principal principal) {
    return principal.getName();
  }

  public static String extractFirstName(Principal principal) {
    if (principal instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      return jwtAuthenticationToken.getTokenAttributes().getOrDefault(GIVEN_NAME, UserAttributes.UNDEFINED).toString();
    } else if (principal instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
      return oAuth2AuthenticationToken.getPrincipal().getName();
    } else {
      throw new IllegalStateException(UNRECOGNIZED_PRINCIPAL_METHOD_ERROR_MESSAGE);
    }
  }

  public static String extractLastName(Principal principal) {
    if (principal instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      return jwtAuthenticationToken.getTokenAttributes().getOrDefault(FAMILY_NAME, UserAttributes.UNDEFINED).toString();
    } else if (principal instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
      return oAuth2AuthenticationToken.getPrincipal().getAttribute(FAMILY_NAME);
    } else {
      throw new IllegalStateException(UNRECOGNIZED_PRINCIPAL_METHOD_ERROR_MESSAGE);
    }
  }

}
