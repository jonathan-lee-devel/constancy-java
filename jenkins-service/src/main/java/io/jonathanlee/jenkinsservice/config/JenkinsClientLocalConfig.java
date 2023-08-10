package io.jonathanlee.jenkinsservice.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@Profile("local")
public class JenkinsClientLocalConfig {

  @Value("#{new Integer('${constancy.jenkins.service.connection.timeout-milliseconds}')}")
  private Integer connectTimeoutMillis;

  @Bean
  WebClient jenkinsWebClient() {
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)
        .responseTimeout(Duration.ofMillis(connectTimeoutMillis))
        .doOnConnected(connection ->
            connection
                .addHandlerLast(new ReadTimeoutHandler(connectTimeoutMillis, TimeUnit.MILLISECONDS))
                .addHandlerLast(new WriteTimeoutHandler(connectTimeoutMillis, TimeUnit.MILLISECONDS))
        );

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }

}
