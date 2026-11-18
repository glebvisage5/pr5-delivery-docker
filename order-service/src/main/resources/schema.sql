CREATE TABLE IF NOT EXISTS customer (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    phone   VARCHAR(20),
    address VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS product (
    id    BIGSERIAL PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    price NUMERIC(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id           BIGSERIAL PRIMARY KEY,
    customer_id  BIGINT      NOT NULL REFERENCES customer (id),
    darkstore_id BIGINT      NOT NULL,
    status       VARCHAR(20) NOT NULL,
    created_at   TIMESTAMP   NOT NULL
);

CREATE TABLE IF NOT EXISTS order_list (
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT  NOT NULL REFERENCES orders (id),
    product_id BIGINT  NOT NULL REFERENCES product (id),
    count      INTEGER NOT NULL
);
