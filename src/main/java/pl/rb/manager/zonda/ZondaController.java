package pl.rb.manager.zonda;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.rb.manager.zonda.model.ZondaRequest;

import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
class ZondaController {

    private final ZondaFacade zondaFacade;

    ZondaController(ZondaFacade zondaFacade) {
        this.zondaFacade = zondaFacade;
    }

    @GetMapping(value = "/summarize")
    private String summarize(Model model) {
        model.addAttribute("zonda", new ZondaRequest());
        this.zondaFacade.loadCommonZondaAttributes(model);
        return "summarize";
    }

    @PostMapping(value = "/summarize")
    private String summarize(Model model, @Valid @ModelAttribute("zonda") ZondaRequest zondaRequest, BindingResult bindingResult) throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        this.zondaFacade.loadCommonZondaAttributes(model);
        if (bindingResult.hasErrors()) {
            return "summarize";
        }
        model.addAttribute("userAction", zondaRequest.getUserAction());
        model.addAttribute("spent", this.zondaFacade.getSpendings(zondaRequest));
        model.addAttribute("fiat", zondaRequest.getFiat());
        return "summarize";
    }
}
