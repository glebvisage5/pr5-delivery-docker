package ru.pirksp.pr5.order;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Доступ к таблицам customer, product, orders, order_list через JdbcClient. */
@Repository
public class OrderRepository {

    private static final String ORDER_SELECT = """
            SELECT o.id, o.darkstore_id, o.status, o.created_at,
                   c.id AS c_id, c.name AS c_name, c.phone AS c_phone, c.address AS c_address
            FROM orders o JOIN customer c ON c.id = o.customer_id
            """;

    private final JdbcClient jdbc;

    public OrderRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Dto.Product> products() {
        return jdbc.sql("SELECT id, name, price FROM product ORDER BY id")
                .query((rs, n) -> new Dto.Product(rs.getLong("id"), rs.getString("name"), rs.getBigDecimal("price")))
                .list();
    }

    public List<Dto.Order> findAll(String instance) {
        return jdbc.sql(ORDER_SELECT + " ORDER BY o.id")
                .query((rs, n) -> order(rs, instance))
                .list();
    }

    public Optional<Dto.Order> findById(long id, String instance) {
        return jdbc.sql(ORDER_SELECT + " WHERE o.id = :id")
                .param("id", id)
                .query((rs, n) -> order(rs, instance))
                .optional();
    }

    public boolean updateStatus(long id, OrderStatus status) {
        return jdbc.sql("UPDATE orders SET status = :status WHERE id = :id")
                .param("status", status.name())
                .param("id", id)
                .update() == 1;
    }

    @Transactional
    public long create(Dto.NewOrder order) {
        var keys = new GeneratedKeyHolder();
        jdbc.sql("INSERT INTO orders (customer_id, darkstore_id, status, created_at) VALUES (:c, :d, 'DRAFT', :t)")
                .param("c", order.customerId())
                .param("d", order.darkstoreId())
                .param("t", LocalDateTime.now())
                .update(keys, "id");
        long id = keys.getKey().longValue();
        for (Dto.NewLine line : order.orderList()) {
            jdbc.sql("INSERT INTO order_list (order_id, product_id, count) VALUES (:o, :p, :n)")
                    .param("o", id).param("p", line.productId()).param("n", line.count())
                    .update();
        }
        return id;
    }

    private Dto.Order order(ResultSet rs, String instance) throws SQLException {
        long id = rs.getLong("id");
        var customer = new Dto.Customer(rs.getLong("c_id"), rs.getString("c_name"),
                rs.getString("c_phone"), rs.getString("c_address"));
        List<Dto.OrderLine> lines = jdbc.sql("""
                        SELECT p.id, p.name, p.price, l.count
                        FROM order_list l JOIN product p ON p.id = l.product_id
                        WHERE l.order_id = :id ORDER BY p.id
                        """)
                .param("id", id)
                .query((r, n) -> {
                    BigDecimal price = r.getBigDecimal("price");
                    int count = r.getInt("count");
                    return new Dto.OrderLine(r.getLong("id"), r.getString("name"), price, count,
                            price.multiply(BigDecimal.valueOf(count)));
                })
                .list();
        BigDecimal total = lines.stream().map(Dto.OrderLine::sum).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Dto.Order(id, customer, rs.getLong("darkstore_id"), OrderStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime(), lines, total, instance);
    }
}
