package pl.rb.manager.service;

import pl.rb.manager.model.dto.UserDto;

public interface IUserService {
    void auth(UserDto userDto);

    void register(UserDto userDto);

    boolean exists(String username);

    void logout();
}
