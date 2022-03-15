package pl.rb.manager.wallet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
class WalletController {

    @GetMapping(value = "/index")
    private String main(Model model, Principal principal) {
        if (principal != null) {
            List<String> cryptos = new ArrayList<>();
            cryptos.add("X");
            cryptos.add("Y");
            model.addAttribute("cryptos", cryptos);
        } else {
            model.addAttribute("cryptos", new ArrayList<>());
        }
        return "index";
    }

    //todo
}
