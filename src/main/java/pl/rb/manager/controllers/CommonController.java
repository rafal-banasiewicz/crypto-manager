package pl.rb.manager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.rb.manager.session.SessionObject;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CommonController {

    private final SessionObject sessionObject;

    public CommonController(SessionObject sessionObject) {
        this.sessionObject = sessionObject;
    }

    @GetMapping(value = "/index")
    public String main(Model model) {
        if (this.sessionObject.isLogged()) {
            List<String> cryptos = new ArrayList<>();
            cryptos.add("X");
            cryptos.add("Y");
            model.addAttribute("cryptos", cryptos);
        } else {
            model.addAttribute("cryptos", new ArrayList<>());
        }
        model.addAttribute("logged", this.sessionObject.isLogged());
        return "index";
    }

}
