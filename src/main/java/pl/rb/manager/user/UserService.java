package pl.rb.manager.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.rb.manager.user.model.User;
import pl.rb.manager.user.model.UserRole;

import java.util.Optional;

@Service
class UserService implements IUserService {

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
