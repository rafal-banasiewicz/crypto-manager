package pl.rb.manager.user;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rb.manager.user.model.UserRole;

interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    UserRole findByRole(String role);
}