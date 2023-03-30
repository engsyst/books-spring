package ua.nure.demo.books.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.nure.demo.books.controller.dto.PageSortFilter;
import ua.nure.demo.books.entity.Book;
import ua.nure.demo.books.entity.Order;
import ua.nure.demo.books.repository.OrderRepository;
import ua.nure.demo.books.service.BookService;

import java.util.List;

@RequestMapping(path = "/admin")
@Controller
public class AdminController {
    static Logger log = LoggerFactory.getLogger(AdminController.class);

    private final OrderRepository orderRepository;
    private final BookService bookService;

    @Autowired
    public AdminController(OrderRepository orderRepository, BookService bookService) {
        this.orderRepository = orderRepository;
        this.bookService = bookService;
    }

    @GetMapping(path = "/orders")
    public String findAllOrders(Model model, @RequestParam int page, @RequestParam int count, @RequestParam String sort) {
//        List<Order> orders = orderRepository.findAll();
//        log.debug("Orders: {}", orders);
//        model.addAttribute("orders", orders);
//        model.addAttribute("search", "");
        return "admin/testorders";
    }

    @GetMapping(path = "/books")
    public String findBooks(Model model, PageSortFilter paging) {
        Page<Book> books = bookService.findAll(paging);

        model.addAttribute("books", books);
        log.debug("Add 'books' model attribute: {}", books);
        paging.setTotalPages(books.getTotalPages());
        log.debug("Books total elements: {}", paging.getTotalPages());
        model.addAttribute("paging", paging);
        log.debug("Add 'paging' model attribute: {}", paging);
        return "admin/books";
    }
}
