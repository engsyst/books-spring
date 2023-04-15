package ua.nure.demo.books.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ua.nure.demo.books.entity.Book;

import java.util.List;
import java.util.Optional;

@Service
public class BookRepositoryImpl implements BookRepository {
    Logger log = LoggerFactory.getLogger(BookRepositoryImpl.class);

    //    private final AuthorRepository authorRepository;
    public static final String BOOKS_TOTAL = "SELECT COUNT(*) FROM book";
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private AuthorRepository authorRepository;

    private final ResultSetExtractor<Book> oneBookExtractor = rs -> new Book(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("isbn"),
            rs.getBigDecimal("price"),
            rs.getInt("amount"),
            rs.getLong("category_id"),
            rs.getString("cover"),
            rs.getString("description"),
            null
    );

    private final RowMapper<Book> findBooksMapper = (rs, rowNum) ->
            oneBookExtractor.extractData(rs);

    private final ResultSetExtractor<Book> getBooksMapper = (rs) -> {
        rs.next();
        return oneBookExtractor.extractData(rs);
    };

    private static final String GET_ALL_BOOKS = "SELECT b.id, b.category_id, b.amount, b.cover, " +
            "b.description, b.isbn, b.price, b.title " +
            "FROM book b";

    @Override
    public Page<Book> findAll(Pageable pageable) {
        String sql = SqlHelper.setPageable(GET_ALL_BOOKS, pageable);
        log.debug("Query: {}", sql);
        List<Book> books = jdbc.query(sql, findBooksMapper);
        Long total = jdbc.query(BOOKS_TOTAL, (rs, rn) -> (rs.getLong(1))).get(0);
        return new PageImpl<>(books, pageable, total == null ? 0 : total);
    }

    @Override
    public Optional<Book> findBookById(Long id) {
        try {
            Book book = jdbc.query("SELECT * FROM book b WHERE b.id = ?", getBooksMapper, id);
            book.setAuthor(authorRepository.findByBookId(id));
            return Optional.of(book);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }
}
