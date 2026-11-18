package ru.pirksp.pr5.delivery;

/** DeliveryStatus: статус доставки, вычисляется по статусу заказа (Order.status). */
public enum DeliveryStatus {
    NOT_READY("Заказ ещё не оформлен"),
    WAITING("Ожидает сборки"),
    ASSEMBLY("В сборке"),
    IN_TRANSIT("Курьер в пути"),
    DELIVERED("Доставлен"),
    CANCELED("Отменён");

    private final String rus;

    DeliveryStatus(String rus) {
        this.rus = rus;
    }

    public String rus() {
        return rus;
    }

    public static DeliveryStatus fromOrderStatus(String orderStatus) {
        return switch (orderStatus) {
            case "DRAFT" -> NOT_READY;
            case "FIXED" -> WAITING;
            case "ASSEMBLY" -> ASSEMBLY;
            case "DELIVERY" -> IN_TRANSIT;
            case "COMPLETED" -> DELIVERED;
            default -> CANCELED;
        };
    }
}
