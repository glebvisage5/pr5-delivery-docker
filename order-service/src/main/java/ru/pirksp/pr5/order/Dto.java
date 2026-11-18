package ru.pirksp.pr5.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Объекты, которые Order Service передаёт по REST. */
public final class Dto {
    private Dto() {
    }

    /** Агрегат Customer. */
    public record Customer(long id, String name, String phone, String address) {
    }

    /** Агрегат Product. */
    public record Product(long id, String name, BigDecimal price) {
    }

    /** OrderList: позиция заказа (product + count). */
    public record OrderLine(long productId, String productName, BigDecimal price, int count, BigDecimal sum) {
    }

    /** Агрегат Order вместе с orderListList и идентификатором экземпляра сервиса. */
    public record Order(long id, Customer customer, long darkstoreId, OrderStatus status,
                        LocalDateTime createdAt, List<OrderLine> orderList, BigDecimal total,
                        String instance) {
    }

    /** Тело запроса PATCH /api/orders/{id}/status. */
    public record StatusChange(OrderStatus status) {
    }

    /** Тело запроса POST /api/orders. */
    public record NewOrder(long customerId, long darkstoreId, List<NewLine> orderList) {
    }

    public record NewLine(long productId, int count) {
    }
}
