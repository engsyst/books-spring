package ua.nure.demo.books.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.nure.demo.books.entity.Book;
import ua.nure.demo.books.entity.Delivery;
import ua.nure.demo.books.entity.Order;
import ua.nure.demo.books.entity.User;
import ua.nure.demo.books.web.common.ShoppingCart;

import java.util.Optional;

public interface OrderService {


    Order createOrder(User user, ShoppingCart<Book> cart, Delivery delivery);

    Optional<Order> findById(Long id);

    Page<Order> findByStatus(String status, Pageable pageable);

    void setStatus(Order order, String status);
}
