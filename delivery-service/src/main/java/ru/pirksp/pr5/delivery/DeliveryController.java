package ru.pirksp.pr5.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/** REST API доставок. Внешний путь через Traefik: /api/deliveries/**. */
@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private static final Logger log = LoggerFactory.getLogger(DeliveryController.class);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final DeliveryService deliveries;
    private final String instance;
    private final AtomicLong counter = new AtomicLong();

    public DeliveryController(DeliveryService deliveries, @Value("${app.instance-id}") String instance) {
        this.deliveries = deliveries;
        this.instance = instance;
    }

    /** Какой экземпляр обработал запрос (для демонстрации балансировки Traefik). */
    @GetMapping(value = "/instance", produces = MediaType.TEXT_PLAIN_VALUE)
    public String instance() {
        long n = counter.incrementAndGet();
        log.info("GET /api/deliveries/instance -> запрос №{} на этом экземпляре", n);
        return "%s  delivery-service  instance=%s  запрос на экземпляре №%d%n"
                .formatted(LocalTime.now().format(TIME), instance, n);
    }

    @GetMapping("/{orderId}")
    public Model.Delivery byOrder(@PathVariable long orderId) {
        log.info("GET /api/deliveries/{}", orderId);
        return deliveries.byOrder(orderId);
    }

    @PostMapping("/{orderId}/start")
    public Model.Delivery start(@PathVariable long orderId) {
        log.info("POST /api/deliveries/{}/start", orderId);
        return deliveries.start(orderId);
    }

    @PostMapping("/{orderId}/complete")
    public Model.Delivery complete(@PathVariable long orderId) {
        log.info("POST /api/deliveries/{}/complete", orderId);
        return deliveries.complete(orderId);
    }
}
