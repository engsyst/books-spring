package ua.nure.demo.books.web.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.nure.demo.books.entity.Book;
import ua.nure.demo.books.entity.Order;
import ua.nure.demo.books.entity.User;
import ua.nure.demo.books.service.BookService;
import ua.nure.demo.books.service.OrderService;
import ua.nure.demo.books.web.NotFoundException;
import ua.nure.demo.books.web.common.ShoppingCart;
import ua.nure.demo.books.web.dto.DeliveryForm;
import ua.nure.demo.books.web.dto.PageSortFilter;
import ua.nure.demo.books.web.dto.UpdateCart;

import java.util.Optional;

@Controller
@Slf4j
@RequestMapping(path = "/client")
public class ClientController {
    private final BookService bookService;
    private final OrderService orderService;

    @Autowired
    public ClientController(BookService bookService, OrderService orderService) {
        this.bookService = bookService;
        this.orderService = orderService;
    }

    private static ShoppingCart<Book> getShoppingCart(HttpSession session) {
        ShoppingCart<Book> cart = (ShoppingCart<Book>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ShoppingCart<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    private static Optional<Object> addSessionAttributeToModel(String attributeName, Model model, HttpSession session) {
        return addSessionAttributeToModel(attributeName, model, session, false);
    }

    private static Optional<Object> addSessionAttributeToModel(String attributeName, Model model,
                                                               HttpSession session, boolean remove) {
        Object attribute = session.getAttribute(attributeName);
        if (attribute != null) {
            model.addAttribute(attributeName, attribute);
        }
        if (remove) {
            session.removeAttribute(attributeName);
        }
        return Optional.ofNullable(attribute);
    }

    @PostMapping(path = "/addtocart")
    public String addToCart(@RequestParam Long id,
                            @RequestParam Integer count,
                            HttpSession session) {
        log.debug("id: {}, count: {}", id, count);
        ShoppingCart<Book> cart = getShoppingCart(session);
        Book book = bookService.getById(id).orElseThrow(() ->
                new NotFoundException("Book not found"));
        cart.add(book, count);
        log.debug("Shopping cart: {}", cart);
        return "redirect:/book";
    }

    @PostMapping(path = "/updatecart")
    public String updateCart(UpdateCart form, HttpSession session) {
        log.debug("Update params: {}", form);
        if (!form.isValid()) {
            throw new IllegalStateException("'update' or 'remove' must be specified");
        }
        ShoppingCart<Book> cart = getShoppingCart(session);
        Book book;
        Long id = form.getUpdate() == null ? form.getRemove() : form.getUpdate();
        try {
            book = bookService.getById(id).orElseThrow(NotFoundException::new);
            if (form.getRemove() != null) {
                cart.remove(book);
                session.setAttribute("info", "Cart was updated");
                log.debug("Book with id: '{}' removed from the cart: {}", id, cart);
            } else {
                // Check availability
                if (book.getAmount() < form.getCount()) {
                    form.setCount(book.getAmount());
                    session.setAttribute("error",
                            "There is only " + book.getAmount() + " books.");
                }
                cart.put(book, form.getCount());
                session.setAttribute("info", "Cart was updated");
                log.debug("Book with id: '{}' updated in the cart: {}", id, cart);
            }
        } catch (NotFoundException e) {
            session.setAttribute("error", "Book was not found.");
        }
        return "redirect:/client/cart";
    }

    @GetMapping(path = "/cart")
    public String cart(Model model, PageSortFilter paging, HttpSession session) {
        ShoppingCart<Book> cart = getShoppingCart(session);
        model.addAttribute("cart", cart);
        log.debug("Add model attribute 'cart': {}", cart);
        model.addAttribute("paging", paging);
        log.debug("Add model attribute 'paging': {}", paging);
        addSessionAttributeToModel("error", model, session, true);
        addSessionAttributeToModel("info", model, session, true);
        return "/client/cart";
    }

    @GetMapping(path = "/delivery")
    public String fillDelivery(DeliveryForm delivery, Model model, HttpSession session) {
        delivery = (DeliveryForm) addSessionAttributeToModel("deliveryForm", model, session)
                .orElse(delivery);
        model.addAttribute("delivery", delivery);
        return "/client/delivery_form";
    }

    @PostMapping(path = "/makeorder")
    public String makeOrder(@Valid DeliveryForm delivery, BindingResult bindingResult,
                            Model model, HttpSession session) {
        if (bindingResult.hasFieldErrors()) {
            // Spring automatically adds to Model 'deliveryForm' and
            // 'org.springframework.validation.BindingResult.deliveryForm'
            log.debug("Binding results: {}", model);
            return "/client/delivery_form";
        }

        ShoppingCart<Book> cart = (ShoppingCart<Book>) session.getAttribute("cart");
        if (cart == null) {
            throw new IllegalStateException("Cannot create order because ShoppingCart not found");
        }
        User user = (User) session.getAttribute("user");
        Order order = orderService.createOrder(user, cart, delivery.asDelivery());
        session.setAttribute("order", order);
        cart.clear();
        return "redirect:/client/order/" + order.getId();
    }

    @PostMapping(path = "/savedelivery")
    public String saveDelivery(DeliveryForm delivery, HttpSession session) {
        session.setAttribute("deliveryForm", delivery);
        return "/client/delivery_form";
    }

    @GetMapping(path = "/order/{id}")
    public String saveDelivery(@PathVariable Long id, Model model) {
        Optional<Order> order = orderService.findById(id);
        log.debug("Order: {}", order);
        model.addAttribute("order", order.orElseThrow(() ->
                new DataRetrievalFailureException("Not found")));
        return "/client/order";
    }
}
