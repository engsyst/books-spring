package ua.nure.demo.books.entity;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor @NoArgsConstructor
public class Book implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;
    private String isbn;
    private BigDecimal price;
    private int amount;
    private Long categoryId;
    private String cover;
    private String description;
    private Set<Author> author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
