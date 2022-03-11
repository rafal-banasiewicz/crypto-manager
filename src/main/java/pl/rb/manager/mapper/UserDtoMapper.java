package pl.rb.manager.mapper;

import org.springframework.stereotype.Service;
import pl.rb.manager.model.User;
import pl.rb.manager.model.dto.UserDto;

@Service
public
class UserDtoMapper {
    public User map(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        return user;
    }
}