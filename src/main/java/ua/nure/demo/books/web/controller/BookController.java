package ua.nure.demo.books.web.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.nure.demo.books.web.common.BookViewConfig;
import ua.nure.demo.books.web.dto.PageSortFilter;
import ua.nure.demo.books.entity.Book;
import ua.nure.demo.books.service.BookService;

@Controller
@RequestMapping(path = "/book")
public class BookController {
    static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;
    private final BookViewConfig bookViewConfig;

    @Autowired
    public BookController(BookService bookService, BookViewConfig bookViewConfig) {
        this.bookService = bookService;
        this.bookViewConfig = bookViewConfig;
    }

    @GetMapping(path = "/{id}")
    public String get(@PathVariable Long id, Model model) {
        if (id == 0) {
            model.addAttribute("book", new Book());
        } else {
            bookService.getById(id).ifPresentOrElse(b ->
                            model.addAttribute("book", b),
                    () -> model.addAttribute("error",
                            "Book not found."));
        }
        log.debug("Add 'book' with id: {} to model", id);
        return "book/book";
    }

    @GetMapping
    public String getAll(PageSortFilter paging, Model model, HttpSession session) {
        paging = paging == null ? new PageSortFilter() : paging;
        paging.setIfNull((PageSortFilter) session.getAttribute("bookPaging"));
        Page<Book> books = bookService.findAll(paging.asPageable());
        paging.setTotalPages(books.getTotalPages());
        log.debug("Add 'books' to model: {}", books);
        model.addAttribute("books", books);
        model.addAttribute("paging", paging);

        // remember last request paging settings
        session.setAttribute("bookPaging", paging);
        log.debug("Set 'paging' to session: {}", paging);
        return "book/books";
    }
}
