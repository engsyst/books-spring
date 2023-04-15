package ua.nure.demo.books.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.nure.demo.books.entity.Order;

import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(Long id);

    Order save(Order order);

    Page<Order> findAll(Pageable pageable);

    Page<Order> findByStatus(String status, Pageable pageable);

    void updateStatus(Order order);

//    @Query(value = "", nativeQuery = true)
//    @Override
//    Page<Order> findAll(Specification<Order> spec, Pageable pageable);
}