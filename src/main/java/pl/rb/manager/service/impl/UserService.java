package pl.rb.manager.service.impl;

import org.springframework.stereotype.Service;
import pl.rb.manager.model.User;
import pl.rb.manager.model.dto.UserDto;
import pl.rb.manager.repository.UserRepository;
import pl.rb.manager.service.IUserService;
import pl.rb.manager.session.SessionObject;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final SessionObject sessionObject;

    public UserService(UserRepository userRepository, SessionObject sessionObject) {
        this.userRepository = userRepository;
        this.sessionObject = sessionObject;
    }

    @Override
    public void auth(UserDto userDto) {
        Optional<User> userOptional = this.userRepository.findUserByUsername(userDto.getUsername());
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(userDto.getPassword())) {
            this.sessionObject.setLogged(true);
        }
    }

    @Override
    public void register(UserDto userDto) {
        User user = User.toUser(userDto);
        this.userRepository.save(user);
    }

    @Override
    public boolean exists(String username) {
        Optional<User> userOptional = this.userRepository.findUserByUsername(username);
        return userOptional.isPresent();
    }

    @Override
    public void logout() {
        this.sessionObject.setLogged(false);
    }
}
