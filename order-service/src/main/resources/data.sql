INSERT INTO customer (id, name, phone, address) VALUES
    (1, 'Иванов Пётр',     '+7 900 111-22-33', 'ул. Ленина, 12, кв. 5'),
    (2, 'Смирнова Анна',   '+7 900 444-55-66', 'пр. Мира, 48, кв. 101'),
    (3, 'Кузнецов Олег',   '+7 900 777-88-99', 'ул. Садовая, 3, кв. 17')
ON CONFLICT (id) DO NOTHING;

INSERT INTO product (id, name, price) VALUES
    (1, 'Яблоки',              229.90),
    (2, 'Молоко 3,2%',          94.50),
    (3, 'Хлеб бородинский',     62.00),
    (4, 'Сыр российский',      389.00),
    (5, 'Кофе молотый',        549.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO orders (id, customer_id, darkstore_id, status, created_at) VALUES
    (1, 1, 1, 'COMPLETED', '2026-11-18 10:15:00'),
    (2, 2, 2, 'ASSEMBLY',  '2026-11-18 12:40:00'),
    (3, 3, 1, 'FIXED',     '2026-11-18 13:05:00'),
    (4, 1, 3, 'DRAFT',     '2026-11-18 13:30:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_list (id, order_id, product_id, count) VALUES
    (1, 1, 1, 2), (2, 1, 3, 1),
    (3, 2, 2, 3), (4, 2, 4, 1), (5, 2, 5, 1),
    (6, 3, 1, 1), (7, 3, 2, 2), (8, 3, 3, 2),
    (9, 4, 5, 2)
ON CONFLICT (id) DO NOTHING;

-- чтобы следующие INSERT через BIGSERIAL не конфликтовали с id из data.sql
SELECT setval('customer_id_seq',   (SELECT MAX(id) FROM customer));
SELECT setval('product_id_seq',    (SELECT MAX(id) FROM product));
SELECT setval('orders_id_seq',     (SELECT MAX(id) FROM orders));
SELECT setval('order_list_id_seq', (SELECT MAX(id) FROM order_list));
