package ru.pirksp.pr5.delivery;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Объекты Delivery Service: то, что приходит из Order Service, и то, что отдаётся клиенту. */
public final class Model {
    private Model() {
    }

    // ---- ответ Order Service ----
    public record Customer(long id, String name, String phone, String address) {
    }

    public record OrderLine(long productId, String productName, BigDecimal price, int count, BigDecimal sum) {
    }

    public record Order(long id, Customer customer, long darkstoreId, String status, LocalDateTime createdAt,
                        List<OrderLine> orderList, BigDecimal total, String instance) {
    }

    // ---- предметная область доставки ----
    public record Address(String city, String street) {
    }

    public record Darkstore(long id, String name, Address address) {
    }

    /** Информация о доставке, которую Delivery Service формирует по данным заказа. */
    public record Delivery(long orderId,
                           DeliveryStatus status,
                           String statusRus,
                           String customer,
                           String phone,
                           Address from,
                           Address to,
                           String darkstore,
                           int items,
                           BigDecimal total,
                           String courier,
                           LocalDateTime estimatedArrival,
                           String deliveryInstance,
                           String orderInstance) {
    }
}
