package pl.rb.manager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CommonController {

    @GetMapping(value = "/index")
    public String main(Model model, Principal principal) {
        if (principal != null) {
            System.out.println(principal.getName());
            List<String> cryptos = new ArrayList<>();
            cryptos.add("X");
            cryptos.add("Y");
            model.addAttribute("cryptos", cryptos);
        } else {
            model.addAttribute("cryptos", new ArrayList<>());
        }
        return "index";
    }

}
