package pl.rb.manager.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.rb.manager.model.User;
import pl.rb.manager.model.UserRole;
import pl.rb.manager.repository.UserRepository;
import pl.rb.manager.repository.UserRoleRepository;
import pl.rb.manager.service.IUserService;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    private static final String DEFAULT_ROLE = "USER";
    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserRoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void addWithDefaultRole(User user) {
        UserRole defaultRole = roleRepository.findByRole(DEFAULT_ROLE);
        user.getRoles().add(defaultRole);
        String passwordHash = passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordHash);
        userRepository.save(user);
    }

    @Override
    public boolean exists(String email) {
        Optional<User> userOptional = this.userRepository.findByEmail(email);
        return userOptional.isPresent();
    }

}
