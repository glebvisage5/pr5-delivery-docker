package ru.pirksp.pr5.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

/** REST API заказов. Внешний путь через Traefik: /api/orders/**. */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderRepository repository;
    private final String instance;

    public OrderController(OrderRepository repository, @Value("${app.instance-id}") String instance) {
        this.repository = repository;
        this.instance = instance;
    }

    @GetMapping
    public List<Dto.Order> all() {
        log.info("GET /api/orders");
        return repository.findAll(instance);
    }

    @GetMapping("/{id}")
    public Dto.Order one(@PathVariable long id) {
        log.info("GET /api/orders/{}", id);
        return repository.findById(id, instance).orElseThrow(() -> notFound(id));
    }

    @GetMapping("/products")
    public List<Dto.Product> products() {
        log.info("GET /api/orders/products");
        return repository.products();
    }

    @PostMapping
    public ResponseEntity<Dto.Order> create(@RequestBody Dto.NewOrder order) {
        long id = repository.create(order);
        log.info("POST /api/orders -> создан заказ {}", id);
        return ResponseEntity.created(URI.create("/api/orders/" + id))
                .body(repository.findById(id, instance).orElseThrow());
    }

    @PatchMapping("/{id}/status")
    public Dto.Order changeStatus(@PathVariable long id, @RequestBody Dto.StatusChange change) {
        if (!repository.updateStatus(id, change.status())) {
            throw notFound(id);
        }
        log.info("PATCH /api/orders/{}/status -> {}", id, change.status());
        return repository.findById(id, instance).orElseThrow();
    }

    private ResponseStatusException notFound(long id) {
        log.warn("заказ {} не найден", id);
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ с id=" + id + " не найден");
    }
}
