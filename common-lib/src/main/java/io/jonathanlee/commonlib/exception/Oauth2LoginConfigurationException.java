package io.jonathanlee.commonlib.exception;

public class Oauth2LoginConfigurationException extends RuntimeException {

  public Oauth2LoginConfigurationException(Exception exception) {
    super(exception);
  }

}
