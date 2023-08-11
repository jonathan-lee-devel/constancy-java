package io.jonathanlee.jenkinsservice.exception;

public class HttpSecurityOauth2LoginConfigurationException extends RuntimeException {

  public HttpSecurityOauth2LoginConfigurationException(Exception exception) {
    super(exception);
  }

}
