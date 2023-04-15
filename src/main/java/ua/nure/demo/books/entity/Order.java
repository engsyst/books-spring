package ua.nure.demo.books.entity;

import lombok.*;
import ua.nure.demo.books.web.common.Priceable;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Priceable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private User user;
    private Date createDate;
    private Date updateDate;
    private String status;
    private Set<Delivery> deliveries;
    private Set<OrderBook> orderBooks;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public BigDecimal getPrice() {
        return orderBooks.stream().map(OrderBook::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
