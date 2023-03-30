package ua.nure.demo.books.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "book_has_order")
public class OrderHasBook implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Id
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "count", nullable = false)
    private Long count;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderHasBook that = (OrderHasBook) o;
        return Objects.equals(bookId, that.bookId) && Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, orderId);
    }
}
