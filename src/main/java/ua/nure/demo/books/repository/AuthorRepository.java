package ua.nure.demo.books.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.nure.demo.books.entity.Author;

import java.util.Set;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
    @Query(value = """
            SELECT a.id, a.name
            FROM author a, author_has_book ab, book b
            WHERE a.id = ab.author_id AND ab.book_id = b.id AND b.id = :id
            """)
    Set<Author> findByBookId(Long id);

}