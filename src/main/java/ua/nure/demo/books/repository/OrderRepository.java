package ua.nure.demo.books.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ua.nure.demo.books.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {

//    @Query(value = "", nativeQuery = true)
//    @Override
//    Page<Order> findAll(Specification<Order> spec, Pageable pageable);
}