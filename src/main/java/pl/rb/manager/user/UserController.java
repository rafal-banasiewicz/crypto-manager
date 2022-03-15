package pl.rb.manager.user;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Controller
class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/login")
    private String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping(value = "/register")
    private String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    private String addUser(Model model, @ModelAttribute @Valid User user, BindingResult bindResult) {
        model.addAttribute("userExist", false);
        if (bindResult.hasErrors()) return "register";
        if (this.userService.exists(user.getEmail())) {
            model.addAttribute("userExist", true);
            return "register";
        } else {
            this.userService.addWithDefaultRole(user);
            return "redirect:/index";
        }
    }

    @GetMapping("/login-error")
    private String login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (session != null) {
            BadCredentialsException ex = (BadCredentialsException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("user", new User());
        return "login";
    }

}
