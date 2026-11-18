# ПР5. Распределённое приложение сервиса доставки в Docker Compose с Traefik

Предметная область — модуль BootcampLabs «BaaS для АС сервиса доставки»: заказ (Order) со статусами
DRAFT, FIXED, CANCELED, ASSEMBLY, DELIVERY, COMPLETED, позиции заказа (OrderList), товары (Product),
клиенты (Customer) и дарксторы.

```
                 Client
                    │  http://localhost
                    ▼
                Traefik  (панель: http://localhost:8090)
                    │
             ┌──────┴───────────────┐
   /api/orders                /api/deliveries
             ▼                      ▼
     Order Service  ◄──────  Delivery Service ×3
             │      RestClient
             ▼
        PostgreSQL
```

## Запуск

```
docker compose up -d        # или docker compose up
docker compose ps           # все контейнеры должны быть (healthy)
docker compose down -v      # остановка и удаление данных БД
```

Параметры окружения (БД, порты, число экземпляров Delivery Service) находятся в `.env`.

## REST API (через Traefik, http://localhost)

| Метод | Путь | Сервис | Описание |
|---|---|---|---|
| GET | /api/orders | Order | все заказы |
| GET | /api/orders/{id} | Order | заказ с позициями и суммой |
| GET | /api/orders/products | Order | товары |
| POST | /api/orders | Order | создать заказ (DRAFT) |
| PATCH | /api/orders/{id}/status | Order | сменить статус заказа |
| GET | /api/deliveries/{orderId} | Delivery | информация о доставке (запрашивает заказ у Order Service) |
| POST | /api/deliveries/{orderId}/start | Delivery | передать заказ курьеру (Order → DELIVERY) |
| POST | /api/deliveries/{orderId}/complete | Delivery | подтвердить вручение (Order → COMPLETED) |
| GET | /api/deliveries/instance | Delivery | какой экземпляр обработал запрос |

## Проверка балансировки и отказа экземпляра (PowerShell)

```
1..9 | ForEach-Object { curl.exe -s http://localhost/api/deliveries/instance }
docker stop pr5-delivery-service-2
1..6 | ForEach-Object { curl.exe -s http://localhost/api/deliveries/instance }
docker compose up -d        # вернуть остановленные экземпляры
```
