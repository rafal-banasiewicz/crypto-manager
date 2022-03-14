package pl.rb.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rb.manager.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}