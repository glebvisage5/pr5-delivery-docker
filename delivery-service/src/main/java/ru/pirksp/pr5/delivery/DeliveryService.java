package ru.pirksp.pr5.delivery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Сценарий: получить заказ из Order Service и сформировать по нему информацию о доставке. */
@Service
public class DeliveryService {

    private static final String CITY = "Москва";

    /** Дарксторы, из которых выполняется доставка. */
    private static final Map<Long, Model.Darkstore> DARKSTORES = Map.of(
            1L, new Model.Darkstore(1, "Даркстор №1", new Model.Address(CITY, "ул. Садовая, 7")),
            2L, new Model.Darkstore(2, "Даркстор №2", new Model.Address(CITY, "пр. Вернадского, 21")),
            3L, new Model.Darkstore(3, "Даркстор №3", new Model.Address(CITY, "ул. Профсоюзная, 64")));

    private static final List<String> COURIERS = List.of("Алексей К.", "Мария С.", "Дмитрий В.");

    private final OrderClient orders;
    private final String instance;

    public DeliveryService(OrderClient orders, @Value("${app.instance-id}") String instance) {
        this.orders = orders;
        this.instance = instance;
    }

    public Model.Delivery byOrder(long orderId) {
        return toDelivery(orders.getOrder(orderId));
    }

    /** Передать заказ курьеру: Order Service переводит заказ в статус DELIVERY. */
    public Model.Delivery start(long orderId) {
        Model.Order order = orders.getOrder(orderId);
        if (!order.status().equals("ASSEMBLY") && !order.status().equals("FIXED")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Заказ " + orderId + " в статусе " + order.status() + " нельзя передать в доставку");
        }
        return toDelivery(orders.changeStatus(orderId, "DELIVERY"));
    }

    /** Подтвердить вручение: заказ переходит в статус COMPLETED. */
    public Model.Delivery complete(long orderId) {
        Model.Order order = orders.getOrder(orderId);
        if (!order.status().equals("DELIVERY")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Заказ " + orderId + " не находится в доставке (статус " + order.status() + ")");
        }
        return toDelivery(orders.changeStatus(orderId, "COMPLETED"));
    }

    private Model.Delivery toDelivery(Model.Order order) {
        DeliveryStatus status = DeliveryStatus.fromOrderStatus(order.status());
        Model.Darkstore ds = DARKSTORES.getOrDefault(order.darkstoreId(),
                new Model.Darkstore(order.darkstoreId(), "Даркстор №" + order.darkstoreId(), new Model.Address(CITY, "—")));
        String courier = status == DeliveryStatus.IN_TRANSIT || status == DeliveryStatus.DELIVERED
                ? COURIERS.get((int) (order.id() % COURIERS.size()))
                : null;
        LocalDateTime eta = switch (status) {
            case WAITING, ASSEMBLY, IN_TRANSIT -> order.createdAt().plusMinutes(30 + 5 * order.orderList().size());
            default -> null;
        };
        int items = order.orderList().stream().mapToInt(Model.OrderLine::count).sum();
        return new Model.Delivery(order.id(), status, status.rus(),
                order.customer().name(), order.customer().phone(),
                ds.address(), new Model.Address(CITY, order.customer().address()), ds.name(),
                items, order.total(), courier, eta, instance, order.instance());
    }
}
