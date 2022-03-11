package pl.rb.manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.rb.manager.model.dto.UserDto;
import pl.rb.manager.service.IUserService;
import pl.rb.manager.session.SessionObject;

import javax.annotation.Resource;
import javax.validation.Valid;

@Controller
public class UserController {

    @Autowired
    IUserService userService;

    @Resource
    SessionObject sessionObject;

    @GetMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("logged", this.sessionObject.isLogged());
        return "login";
    }

    @PostMapping(value = "/login")
    public String auth(Model model, @Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult) {
        model.addAttribute("logged", this.sessionObject.isLogged());
        if (bindingResult.hasErrors()) {
            return "login";
        }
        this.userService.auth(userDto);
        if (this.sessionObject.isLogged()) {
            return "redirect:/index";
        }
        return "redirect:login";

    }

    @GetMapping(value = "/register")
    public String register(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("logged", this.sessionObject.isLogged());
        return "register";
    }

    @PostMapping("/register")
    String register(Model model, @Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult) {
        model.addAttribute("logged", this.sessionObject.isLogged());
        if (bindingResult.hasErrors() || userService.exists(userDto.getUsername())) {
            return "register";
        } else {
            userService.register(userDto);
            return "redirect:login";
        }
    }

    @GetMapping(value = "/logout")
    public String logout() {
        this.userService.logout();
        return "redirect:index";

    }

}
