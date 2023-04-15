package ua.nure.demo.books.web.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.nure.demo.books.entity.Book;
import ua.nure.demo.books.entity.Order;
import ua.nure.demo.books.service.BookService;
import ua.nure.demo.books.service.OrderService;
import ua.nure.demo.books.service.OrderStatusFlow;
import ua.nure.demo.books.web.NotFoundException;
import ua.nure.demo.books.web.dto.BookForm;
import ua.nure.demo.books.web.dto.PageSortFilter;

@RequestMapping(path = "/admin")
@Controller
public class AdminController {
    static Logger log = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private final OrderStatusFlow orderStatusFlow;

    private final OrderService orderService;
    private final BookService bookService;

    @Autowired
    public AdminController(OrderStatusFlow orderStatusFlow, OrderService orderService, BookService bookService, BookService bookService1) {
        this.orderStatusFlow = orderStatusFlow;
        this.orderService = orderService;
        this.bookService = bookService1;
    }

    @GetMapping(path = "/order")
    public String findAllOrders(PageSortFilter paging, Model model, HttpSession session) {
        paging = setPageSortFilter(paging, session);
        Page<Order> orders = orderService.findByStatus(paging.getFilter(), paging.asPageable());
        log.debug("Orders: {}", orders);
        paging.setTotalPages(orders.getTotalPages());
        model.addAttribute("orders", orders.toList());
        model.addAttribute("paging", paging);

        // remember last request paging settings
        paging.setIfNull((PageSortFilter) session.getAttribute("orderPaging"));
        session.setAttribute("orderPaging", paging);
        log.debug("Set 'orderPaging' to session: {}", paging);
        return "admin/orders";
    }

    private static PageSortFilter setPageSortFilter(PageSortFilter paging, HttpSession session) {
        paging = paging == null ? new PageSortFilter() : paging;
        paging.setIfNull((PageSortFilter) session.getAttribute("orderPaging"));
        return paging;
    }

    @GetMapping(path = "/order/{id}")
    public String ordersDetails(@PathVariable Long id, @RequestParam String action,
                                @RequestParam(required = false) String status, Model model) {
        log.debug("Order id: {}", id);
        log.debug("Action: {}", action);
        orderService.findById(id).ifPresentOrElse(o -> {
            model.addAttribute("order", o);
            model.addAttribute("status", status);
        }, () -> model.addAttribute("error",
                "Order with No: '" + id + "' was not found"));
        return "fragments/order_details";
    }

    @PostMapping(path = "/order/{id}/status")
    public String updateOrdersStatus(@PathVariable Long id, @RequestParam String status,
                                     PageSortFilter paging, Model model, HttpSession session) {
        log.debug("Order id: {}", id);
        Order order = orderService.findById(id)
                .orElseThrow(() -> new NotFoundException("Order with id: '" + id
                        + "' was not found"));
//        setPageSortFilter(paging, session);
        String currentStatus = order.getStatus();
        orderService.setStatus(order, status);
        paging.setIfNull((PageSortFilter) session.getAttribute("orderPaging"));
        Page<Order> orders = orderService.findByStatus(currentStatus, paging.asPageable());
//        model.addAttribute("paging", paging);
        model.addAttribute("orders", orders.toList());
        return "redirect:/admin/order?" + paging.asRequestParameters();
    }

    @GetMapping(path = "/book/{id}")
    public String getBook(@PathVariable Long id, Model model) {
        if (id == 0) {
            model.addAttribute("book", new Book());
        } else {
            bookService.getById(id).ifPresentOrElse(
                    book -> model.addAttribute("book", book),
                    () -> model.addAttribute("error",
                            "Book not found."));
        }
        log.debug("Add 'book' with id: {} to model", id);
        return "/fragments/book_form";
    }

    @PostMapping(path = "/book/{id}/update")
    public String updateBook(@PathVariable Long id, BookForm bookForm, BindingResult errors, HttpSession session) {
        log.debug("Book form: {}", bookForm);
//        model.addAttribute("paging", session.getAttribute("paging"));
//        PageSortFilter paging = (PageSortFilter) session.getAttribute("bookPaging");
//        Page<Book> books = bookService.findAll(paging.asPageable());
//        log.debug("Books: {}", books);
//        model.addAttribute("books", books);
        return "redirect:/book";
    }
}
