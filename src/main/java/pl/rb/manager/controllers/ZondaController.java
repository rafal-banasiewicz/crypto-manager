package pl.rb.manager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.rb.manager.model.zonda.ZondaRequest;
import pl.rb.manager.service.IZondaService;
import pl.rb.manager.session.SessionObject;

import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static pl.rb.manager.utils.CommonHelper.loadCommonZondaAttributes;

@Controller
public class ZondaController {

    private final IZondaService zondaService;
    private final SessionObject sessionObject;

    public ZondaController(IZondaService zondaService, SessionObject sessionObject) {
        this.zondaService = zondaService;
        this.sessionObject = sessionObject;
    }

    @GetMapping(value = "/summarize")
    public String summarize(Model model) {
        model.addAttribute("zonda", new ZondaRequest());
        loadCommonZondaAttributes(model, this.sessionObject);
        return "summarize";
    }

    @PostMapping(value = "/summarize")
    public String summarize(Model model, @Valid @ModelAttribute("zonda") ZondaRequest zondaRequest, BindingResult bindingResult) throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        loadCommonZondaAttributes(model, this.sessionObject);
        if (bindingResult.hasErrors()) {
            return "summarize";
        }
        model.addAttribute("userAction", zondaRequest.getUserAction());
        model.addAttribute("spent", this.zondaService.getSpendings(zondaRequest));
        model.addAttribute("fiat", zondaRequest.getFiat());
        return "summarize";
    }
}
