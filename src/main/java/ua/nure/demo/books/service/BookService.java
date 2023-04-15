package ua.nure.demo.books.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.nure.demo.books.entity.Book;

import java.util.Optional;

public interface BookService {
    Optional<Book> getById(Long id);

    Page<Book> findAll(Pageable paging);
}
