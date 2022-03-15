package pl.rb.manager.user;

interface IUserService {
    void addWithDefaultRole(User user);

    boolean exists(String email);
}
