package ua.nure.demo.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ua.nure.demo.books.entity.Author;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {
//    @Query(value = "SELECT a.id, a.name " +
//            "FROM author a, author_has_book ab " +
//            "WHERE ab.author_id = a.id AND ab.book_id IN ?1", nativeQuery = true)
//    Optional<List<Author>> findByBookIdInRange(List<Long> ids);

    @Query(value = "SELECT a.id, a.name " +
            "FROM author a, author_has_book ab, book b " +
            "WHERE ab.book_id = b.id AND a.id = ab.author_id AND b.id = ?1", nativeQuery = true)
    Set<Author> findByBookId(Long bookId);
}