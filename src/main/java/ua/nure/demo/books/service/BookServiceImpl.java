package ua.nure.demo.books.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ua.nure.demo.books.entity.Book;
import ua.nure.demo.books.repository.AuthorRepository;
import ua.nure.demo.books.repository.BookRepository;

import java.util.Optional;

@Repository
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public Page<Book> findAll(Pageable paging) {

        Page<Book> books = bookRepository.findAll(paging);
        books.forEach(b -> b.setAuthor(authorRepository.findByBookId(b.getId())));
        return books;
    }

    public Optional<Book> getById(Long id) {
        return bookRepository.findBookById(id);
    }
}