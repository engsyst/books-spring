package ua.nure.demo.books.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ua.nure.demo.books.entity.Author;
import ua.nure.demo.books.entity.Book;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookForm implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    @NotBlank
    private String title;
    @Pattern(regexp = "ISBN-\\d{5}-\\d{4}")
    private String isbn;
    @Min(0)
    private BigDecimal price;
    @Min(0)
    private int amount;
    private Long categoryId;
    private String cover;
    private String description;
    private String author;

    public Set<Author> getAuthors() {
        return Arrays.stream(author.split(",\\s*"))
                .map(s -> new Author(0L, s))
                .collect(Collectors.toSet());
    }

    public Book asBook() {
        return new Book(id, title, isbn, price, amount, categoryId, cover, description,
                new LinkedHashSet<>(getAuthors()));
    }

    public BookForm fromBook(Book book) {
        return new BookForm(book.getId(), book.getTitle(), book.getIsbn(),
                book.getPrice(), book.getAmount(), book.getCategoryId(),
                book.getCover(), book.getDescription(),
                book.getAuthor().toString());
    }
}
