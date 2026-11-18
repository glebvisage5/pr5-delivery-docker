package ru.pirksp.pr5.order;

/** Статус заказа из модели DataSpace модуля «BaaS для АС сервиса доставки». */
public enum OrderStatus {
    DRAFT, FIXED, CANCELED, ASSEMBLY, DELIVERY, COMPLETED
}
