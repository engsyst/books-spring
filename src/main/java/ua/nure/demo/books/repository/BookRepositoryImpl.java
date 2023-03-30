package ua.nure.demo.books.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ua.nure.demo.books.entity.Book;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookRepositoryImpl implements BookRepository {
    Logger log = LoggerFactory.getLogger(BookRepositoryImpl.class);

    private final AuthorRepository authorRepository;
    public static final String BOOKS_TOTAL = "SELECT COUNT(*) FROM book";
    private final JdbcTemplate jdbc;
    private final ResultSetExtractor<Book> getBookExtractor = rs -> new Book(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("isbn"),
            rs.getBigDecimal("price"),
            rs.getInt("count"),
            rs.getLong("category_id"),
            rs.getString("cover"),
            rs.getString("description"),
            null
    );

    private final RowMapper<Book> findBooksMapper = (rs, rowNum) ->
            getBookExtractor.extractData(rs);

    @Autowired
    public BookRepositoryImpl(AuthorRepository authorRepository, JdbcTemplate jdbc) {
        this.authorRepository = authorRepository;
        this.jdbc = jdbc;
    }

    String getBooks = "SELECT b.id, b.category_id, b.count, b.cover, " +
            "b.description, b.isbn, b.price, b.title " +
            "FROM book b ORDER BY ? LIMIT ? OFFSET ?";

    @Override
    public Page<Book> findAll(Pageable p) {
        String order = p.getSort().stream()
                .map(o -> o.getProperty() + " " + o.getDirection().name())
                .collect(Collectors.joining(", "));
        log.debug("Sort: {}", order);
        List<Book> books = jdbc.query(getBooks, findBooksMapper,
                order, p.getPageSize(), p.getOffset());
        books.forEach(b -> b.setAuthor(authorRepository.findAuthorByBookId(b.getId())));
        Long count = jdbc.query(BOOKS_TOTAL, (rs, rn) -> (rs.getLong(1))).get(0);
        return new PageImpl<>(books, p, count == null ? 0 : count);
    }

    @Override
    public Book getBookById(Long id) {
        List<Book> books = jdbc.query("SELECT * FROM book b WHERE b.id = ?", findBooksMapper, id);
        if (books.isEmpty()) {
            throw new IllegalArgumentException("Book was not found for id: " + id);
        }
        Book book = books.get(0);
        book.setAuthor(authorRepository.findAuthorByBookId(id));
        return book;
    }
}
