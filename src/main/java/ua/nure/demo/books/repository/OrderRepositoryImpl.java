package ua.nure.demo.books.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.nure.demo.books.entity.Delivery;
import ua.nure.demo.books.entity.Order;
import ua.nure.demo.books.entity.OrderBook;
import ua.nure.demo.books.entity.User;

import java.sql.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Slf4j
public class OrderRepositoryImpl implements OrderRepository {
    static final String INSERT_ORDER = "INSERT INTO `order` (user_id) VALUES (?)";
    private static final String INSERT_ORDER_BOOK = "INSERT INTO order_book (order_id, book_id, count) " +
            "VALUES (?, ?, ?)";
    private static final String INSERT_DELIVERY =
            "INSERT INTO `delivery` (name, phone, email, address, description, order_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
    public static final String ORDERS_TOTAL = "SELECT COUNT(*) FROM `order`";
    private static final String GET_ORDER_BY_ID =
            "SELECT o.id, o.user_id, o.operator_id, " +
                    "o.create_date, o.update_date, o.status " +
                    "FROM `order` o WHERE o.id = ?";
    private static final String FIND_ALL_ORDER =
            "SELECT o.id, o.user_id, o.operator_id, " +
                    "o.create_date, o.update_date, o.status " +
                    "FROM `order` o ";
    private static final String FIND_BY_STATUS =
            "SELECT o.id, o.user_id, o.operator_id, " +
                    "o.create_date, o.update_date, o.status " +
                    "FROM `order` o " +
                    "WHERE o.status = ?";
    private static final String GET_DELIVERY_BY_ORDER_ID =
            "SELECT d.id, d.name, d.phone, d.email, " +
                    "d.address, d.description, d.order_id " +
                    "FROM `delivery` d WHERE d.order_id = ?";
    private static final String GET_ORDER_BOOK_BY_ORDER_ID =
            "SELECT ob.order_id, ob.book_id, ob.count, ob.price " +
                    "FROM order_book ob, `order` o " +
                    "WHERE ob.order_id = o.id AND o.id = ?";

    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private BookRepository bookRepository;
    private final RowMapper<OrderBook> orderBookRowMapper = (rs, rowNum) ->
            orderBookResultSetExtractor(rs);

    private OrderBook orderBookResultSetExtractor(ResultSet rs) throws SQLException {
        return new OrderBook(rs.getLong("order_id"),
                bookRepository.findBookById(rs.getLong("book_id"))
                        .orElse(null),
                rs.getInt("count"),
                rs.getBigDecimal("price")
        );
    }

    private final RowMapper<Order> orderRowMapper = (rs, rn) -> orderResultSetExtractor(rs);

    private final ResultSetExtractor<Order> orderResultSetExtractor = (rs) -> {
        rs.next();
        return orderResultSetExtractor(rs);
    };

    private Order orderResultSetExtractor(ResultSet rs) throws SQLException {
        return new Order(rs.getLong("id"),
                null,
                rs.getDate("create_date"),
                rs.getDate("update_date"),
                rs.getString("status"), null, null);
    }

    private final RowMapper<Delivery> deliveryRowMapper = (rs, rowNum) ->
            deliveryResultSetExtractor(rs);

    private Delivery deliveryResultSetExtractor(ResultSet rs) throws SQLException {
        return new Delivery(rs.getLong("id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getString("description"),
                rs.getLong("order_id"));
    }

    @Override
    public Optional<Order> findById(Long id) {
        try {
            Order order = jdbc.query(GET_ORDER_BY_ID,
                    orderResultSetExtractor,
                    new SqlParameterValue(Types.BIGINT, id));
            Set<Delivery> deliveries = getDeliveries(id);
            order.setDeliveries(deliveries);

            Set<OrderBook> orderBooks = getOrderBooks(id);
            order.setOrderBooks(orderBooks);

            return Optional.of(order);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    private Set<OrderBook> getOrderBooks(Long id) {
        return new LinkedHashSet<>(jdbc.query(
                GET_ORDER_BOOK_BY_ORDER_ID,
                orderBookRowMapper, new SqlParameterValue(Types.BIGINT, id)));
    }

    private Set<Delivery> getDeliveries(Long id) {
        return new LinkedHashSet<>(jdbc.query(
                GET_DELIVERY_BY_ORDER_ID,
                deliveryRowMapper,
                new SqlParameterValue(Types.BIGINT, id)));
    }

    @Transactional
    @Override
    public Order save(Order order) {
        // order
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update((Connection con) -> {
            PreparedStatement ps = con.prepareStatement(INSERT_ORDER, PreparedStatement.RETURN_GENERATED_KEYS);
            User user = order.getUser();
            if (user != null) {
                ps.setObject(1, user.getId(), Types.BIGINT);
            } else {
                ps.setNull(1, Types.BIGINT);
            }
            return ps;
        }, keyHolder);
        order.setId(keyHolder.getKey().longValue());

        // order books
        for (OrderBook book : order.getOrderBooks()) {
            jdbc.update(INSERT_ORDER_BOOK, order.getId(), book.getBook().getId(), book.getCount());
        }

        // delivery
        for (Delivery delivery : order.getDeliveries()) {
            // "(name, phone, email, address, description, user_id) "
            jdbc.update(INSERT_DELIVERY, delivery.getName(), delivery.getPhone(),
                    delivery.getEmail(), delivery.getAddress(), delivery.getDescription(), order.getId());
        }
        return order;
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        String sql = SqlHelper.setPageable(FIND_ALL_ORDER, pageable);
        List<Order> orders = jdbc.query(sql, orderRowMapper);
        orders.forEach(o -> o.setOrderBooks(getOrderBooks(o.getId())));
        orders.forEach(o -> o.setDeliveries(getDeliveries(o.getId())));
        Long total = jdbc.query(ORDERS_TOTAL, (rs, rn) -> (rs.getLong(1))).get(0);
        return new PageImpl<>(orders, pageable, total == null ? 0 : total);
    }

    @Override
    public Page<Order> findByStatus(String status, Pageable pageable) {
        String sql = SqlHelper.setPageable(FIND_BY_STATUS, pageable);
        log.debug("Query: {}", sql);
        List<Order> orders = jdbc.query(sql, orderRowMapper, status);
        orders.forEach(o -> o.setOrderBooks(getOrderBooks(o.getId())));
        orders.forEach(o -> o.setDeliveries(getDeliveries(o.getId())));
        Long total = jdbc.query(ORDERS_TOTAL, (rs, rn) -> (rs.getLong(1))).get(0);
        return new PageImpl<>(orders, pageable, total == null ? 0 : total);
    }

    private static final String UPDATE_STATUS = "UPDATE `order` o SET o.status = ? WHERE o.id = ?";

    @Override
    public void updateStatus(Order order) {
        jdbc.update(UPDATE_STATUS, order.getStatus(), order.getId());
    }
}
