package ru.pirksp.pr5.delivery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/** RestClient для обращения к Order Service. Адрес берётся из переменной окружения ORDER_SERVICE_URL. */
@Configuration
public class OrderClientConfig {

    @Bean
    RestClient orderRestClient(RestClient.Builder builder,
                               @Value("${app.order-service.url}") String baseUrl,
                               @Value("${app.order-service.timeout:2s}") Duration timeout) {
        // HTTP-клиент JDK (поддерживает PATCH) с таймаутами подключения и ответа
        var http = HttpClient.newBuilder().connectTimeout(timeout).build();
        var factory = new JdkClientHttpRequestFactory(http);
        factory.setReadTimeout(timeout);
        return builder
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }
}
