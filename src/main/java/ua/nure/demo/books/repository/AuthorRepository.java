package ua.nure.demo.books.repository;

import ua.nure.demo.books.entity.Author;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AuthorRepository {
//    @Query(value = "SELECT a.id, a.name " +
//            "FROM author a, author_has_book ab " +
//            "WHERE ab.author_id = a.id and ab.book_id IN ?1", nativeQuery = true)
    Set<Author> findAuthorByBookId(Long id);
}