package ua.nure.demo.books.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ua.nure.demo.books.entity.Author;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class AuthorRepositoryImpl implements AuthorRepository {
    private final JdbcTemplate jdbc;

    static final String getAuthorsByBookId = "SELECT a.id, a.name " +
            "FROM author a, author_has_book ab, book b " +
            "WHERE ab.book_id = b.id AND a.id = ab.author_id AND b.id = ?";

    @Autowired
    public AuthorRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Set<Author> findAuthorByBookId(Long id) {
        return new HashSet<>(jdbc.query(getAuthorsByBookId,
                (rs, row) -> new Author(
                        rs.getLong("id"), rs.getString("name")),
                id));
    }
}
