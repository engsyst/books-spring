package ua.nure.demo.books.entity;

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
public class OrderHasBook implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long bookId;
    private Long orderId;
    private Long count;
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
