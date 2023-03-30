package ua.nure.demo.books.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
//@ToString
@Entity
@Table(name = "book", indexes = {
        @Index(name = "idx_book_title", columnList = "title"),
        @Index(name = "idx_book_price", columnList = "price"),
})
public class Book implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "count", nullable = false)
    private Long count;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "cover")
    private String cover;

    @Column(name = "description")
    private String description;

    @ManyToMany
    @JoinTable(name = "author_has_book")
    private Set<Author> author;

    @Override
    public String toString() {
        return super.toString() +
                " Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", isbn='" + isbn + '\'' +
                ", price=" + price +
                ", count=" + count +
                ", categoryId=" + categoryId +
                ", cover='" + cover + '\'' +
                ", description='" + description + '\'' +
                ", author=" + author +
                '}';
    }
}
