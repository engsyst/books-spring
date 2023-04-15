package ua.nure.demo.books.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.nure.demo.books.entity.*;
import ua.nure.demo.books.repository.OrderRepository;
import ua.nure.demo.books.repository.UserRepository;
import ua.nure.demo.books.web.common.ShoppingCart;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderStatusFlow orderFlow;


    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, OrderStatusFlow orderFlow) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderFlow = orderFlow;
    }

    @Override
    public Order createOrder(User user, ShoppingCart<Book> cart, Delivery delivery) {
        Set<OrderBook> orderBooks = cart.entrySet().stream()
                .map(entry -> new OrderBook(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
        Order order = new Order(null, user,
                null, null, null, Set.of(delivery), orderBooks);
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
    @Override
    public Page<Order> findByStatus(String status, Pageable pageable) {
        Page<Order> orders;
        if (status == null || status.isBlank()) {
            orders = orderRepository.findAll(pageable);
        } else {
            orders = orderRepository.findByStatus(status, pageable);
        }
        orders.forEach(o -> o.setUser(userRepository.findByOrderId(o.getId()).orElse(null)));
        return orders;
    }

    @Override
    public void setStatus(Order order, String status) {
        List<String> nextStatuses = orderFlow.getStatusFlow().get(order.getStatus());
        if (nextStatuses == null || !nextStatuses.contains(status)) {
            throw new IllegalArgumentException("Status '" + status + "' not allowed");
        }
        order.setStatus(status);
        orderRepository.updateStatus(order);
    }
}
