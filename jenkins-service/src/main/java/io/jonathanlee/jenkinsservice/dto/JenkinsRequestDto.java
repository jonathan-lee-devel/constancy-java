package io.jonathanlee.jenkinsservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JenkinsRequestDto {

  @NotNull
  private String username;

  @NotNull
  private String token;

  @Setter(AccessLevel.NONE)
  @NotNull
  private String host;

  @Setter(AccessLevel.NONE)
  @NotNull
  private String urlEnd;

  public void setHost(String host) {
    // Remove trailing slash from host URL
    this.host = (host.endsWith("/")) ? host.substring(0, host.lastIndexOf("/")) : host;
  }

  public void setUrlEnd(String urlEnd) {
    // Remove initial slash (if present) and trailing slash from host URL
    urlEnd = urlEnd.startsWith("/") ? urlEnd.substring(1) : urlEnd;
    this.urlEnd = urlEnd.endsWith("/") ? urlEnd.substring(0, urlEnd.lastIndexOf("/")) : urlEnd;
  }

}
