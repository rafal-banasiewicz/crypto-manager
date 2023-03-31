package pl.rb.manager.zonda;

import com.itextpdf.text.DocumentException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rb.manager.model.ExchangeRequest;

import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

@Controller
class ZondaController {

    private final ZondaFacade zondaFacade;

    ZondaController(ZondaFacade zondaFacade) {
        this.zondaFacade = zondaFacade;
    }

    @GetMapping(value = "/summarize")
    private String summarize(Model model) {
        model.addAttribute("exchangeRequest", new ExchangeRequest());
        this.zondaFacade.loadCommonZondaAttributes(model);
        return "summarize";
    }

    @PostMapping(value = "/summarize")
    private String summarize(Model model, @Valid @ModelAttribute("exchangeRequest") ExchangeRequest exchangeRequest, BindingResult bindingResult) throws NoSuchAlgorithmException, IOException, InvalidKeyException, DocumentException, ParseException {
        this.zondaFacade.loadCommonZondaAttributes(model);
        if (bindingResult.hasErrors()) {
            return "summarize";
        }
        model.addAttribute("userAction", exchangeRequest.getUserAction());
        model.addAttribute("spent", this.zondaFacade.getSpendings(exchangeRequest));
        return "summarize";
    }
}
