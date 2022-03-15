package pl.rb.manager.user;

import org.springframework.data.jpa.repository.JpaRepository;

interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    UserRole findByRole(String role);
}