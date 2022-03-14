package pl.rb.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rb.manager.model.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    UserRole findByRole(String role);
}