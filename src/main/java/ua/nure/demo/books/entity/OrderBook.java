package ua.nure.demo.books.entity;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor @AllArgsConstructor
public class OrderBook implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Book book;
    private Integer count;
    private BigDecimal price;

    public OrderBook(Book book, Integer count) {
        this.book = book;
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderBook that = (OrderBook) o;
        return Objects.equals(book.getId(), that.book.getId()) && Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book.getId(), orderId);
    }
}
