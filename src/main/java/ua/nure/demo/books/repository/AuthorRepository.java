package ua.nure.demo.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ua.nure.demo.books.entity.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {
    @Query(value = "SELECT a.id, a.name " +
            "FROM author a, author_has_book ab " +
            "WHERE ab.author_id = a.id and ab.book_id IN ?1", nativeQuery = true)
    Optional<List<Author>> findByBookIdInRange(List<Long> ids);
}