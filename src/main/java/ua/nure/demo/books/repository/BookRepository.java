package ua.nure.demo.books.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.nure.demo.books.entity.Book;

import java.util.Optional;

/**
 * WARN! Spring-data-jdbs does not support @Query for Pageable parameters,
 * use JdbcTemplate instead
 */
public interface BookRepository {
//            "SELECT DISTINCT b.id, b.category_id, b.deliveryForm, b.cover, b.description, " +
//            "b.isbn, b.price, b.title, a.id author_id, concat_authors(b.id) name " +
//            "FROM bookshop.book b " +
//            "JOIN author_has_book ab ON b.id = ab.book_id " +
//            "JOIN author a ON a.id = ab.author_id " +
//            "GROUP BY b.id HAVING COUNT(*) > 0",
//            countQuery = "SELECT COUNT(*) FROM book",
//            nativeQuery = true)
    Page<Book> findAll(Pageable p);

    Optional<Book> findBookById(Long id);
}