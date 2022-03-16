package pl.rb.manager.user;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rb.manager.user.model.User;

import java.util.Optional;

interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}