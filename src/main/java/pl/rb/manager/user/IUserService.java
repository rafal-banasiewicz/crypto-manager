package pl.rb.manager.user;

import pl.rb.manager.user.model.User;

interface IUserService {
    void addWithDefaultRole(User user);

    boolean exists(String email);
}
