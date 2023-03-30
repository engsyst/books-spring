package ua.nure.demo.books.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.nure.demo.books.controller.dto.PageSortFilter;
import ua.nure.demo.books.entity.Book;
import ua.nure.demo.books.repository.AuthorRepository;
import ua.nure.demo.books.repository.BookRepository;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    @Value("${app.web.books.default_sort_field}")
    private String defaultSortField;
    @Value("${app.web.books.default_items_per_page}")
    private int defaultItemsPerPage;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public Page<Book> findAll(PageSortFilter paging) {
        String sortField = paging.getSortField();
        paging.setSortField(sortField == null || sortField.isBlank() ? defaultSortField : sortField);
        int itemsCount = paging.getPageItemsCount();
        paging.setPageItemsCount(itemsCount != 0 ? itemsCount : defaultItemsPerPage);

        PageRequest pageRequest = PageRequest.of(paging.getPageNumber(), paging.getPageItemsCount(),
                Sort.by(Sort.Direction.fromString(paging.getSortOrder()), paging.getSortField()));
        return bookRepository.findAll(pageRequest);
    }

    public Book getBookById(Long id) {
        return bookRepository.getBookById(id);
    }
}