package ua.nure.demo.books.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ua.nure.demo.books.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    /*
            SELECT u.id, u.login, u.password, u.role, u.email, u.phone, u.name, u.address, u.avatar, u.description
            FROM user u, `order` o
            WHERE o.user_id = u.id AND o.id = :id
    */
    @Query(value = """
            SELECT u.id, u.login, u.password, u.role, u.email, u.phone, GROUP_CONCAT(d.name SEPARATOR ', ') name, GROUP_CONCAT(d.address SEPARATOR ', ') address, u.avatar, u.description
            FROM user u
            JOIN `order` o ON u.id = o.user_id
            JOIN delivery d ON d.order_id = o.id
            WHERE o.id = :id
            """)
    Optional<User> findByOrderId(Long id);
}