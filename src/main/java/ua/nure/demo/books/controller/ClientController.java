package ua.nure.demo.books.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.nure.demo.books.controller.dto.PageSortFilter;
import ua.nure.demo.books.entity.Book;
import ua.nure.demo.books.service.BookService;

@Controller
@RequestMapping(path = "/client")
public class ClientController {
    static final Logger log = LoggerFactory.getLogger(ClientController.class);
    private final BookService bookService;

    @Autowired
    public ClientController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(path = "/book/{id}")
    public String get(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        log.debug("Add 'book' with id: {} to model: {}", id, book);
        return "book";
    }

    @GetMapping(path = "/book")
    public String get(Model model, PageSortFilter paging) {
        Page<Book> books = bookService.findAll(paging);

        model.addAttribute("books", books);
        log.debug("Add 'books' to model: {}", books);
        paging.setTotalPages(books.getTotalPages());
        log.debug("'books' total elements: {}", paging.getTotalPages());
        model.addAttribute("paging", paging);
        log.debug("Add 'paging' to model: {}", paging);
        return "books";
    }
}
