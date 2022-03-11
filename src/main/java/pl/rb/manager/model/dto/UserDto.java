package pl.rb.manager.model.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserDto {

    @NotNull
    @Size(min = 2, max = 50, message = "Username '${validatedValue}' is invalid. Username must be between {min} and {max} characters long")
    private String username;
    @NotNull
    @Size(min = 2, max = 50, message = "Password '${validatedValue}' is invalid. Password must be between {min} and {max} characters long")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}