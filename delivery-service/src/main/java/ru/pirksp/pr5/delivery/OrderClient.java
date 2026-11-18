package ru.pirksp.pr5.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/** Межсервисное взаимодействие: Delivery Service → Order Service по HTTP (RestClient). */
@Component
public class OrderClient {

    private static final Logger log = LoggerFactory.getLogger(OrderClient.class);

    private final RestClient client;

    public OrderClient(RestClient orderRestClient) {
        this.client = orderRestClient;
    }

    public Model.Order getOrder(long id) {
        log.info("→ order-service GET /api/orders/{}", id);
        try {
            Model.Order order = client.get()
                    .uri("/api/orders/{id}", id)
                    .retrieve()
                    .onStatus(s -> s.value() == 404, (req, res) -> {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ с id=" + id + " не найден в Order Service");
                    })
                    .body(Model.Order.class);
            log.info("← order-service: заказ {} ({}), экземпляр order-service {}", id, order.status(), order.instance());
            return order;
        } catch (ResourceAccessException e) {
            log.error("order-service недоступен: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Order Service недоступен");
        }
    }

    public Model.Order changeStatus(long id, String status) {
        log.info("→ order-service PATCH /api/orders/{}/status {}", id, status);
        try {
            return client.patch()
                    .uri("/api/orders/{id}/status", id)
                    .body(Map.of("status", status))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ с id=" + id + " не найден в Order Service");
                    })
                    .body(Model.Order.class);
        } catch (ResourceAccessException e) {
            log.error("order-service недоступен: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Order Service недоступен");
        }
    }
}
