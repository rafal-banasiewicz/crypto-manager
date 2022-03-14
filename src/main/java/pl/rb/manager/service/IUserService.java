package pl.rb.manager.service;

import pl.rb.manager.model.User;

public interface IUserService {
    void addWithDefaultRole(User user);

    boolean exists(String email);
}
