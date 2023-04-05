package pl.rb.manager.exchange;

import com.itextpdf.text.DocumentException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.rb.manager.exchange.binance.BinanceFacade;
import pl.rb.manager.exchange.zonda.ZondaFacade;
import pl.rb.manager.model.ExchangeRequest;

import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

@Controller
record ExchangeController(ZondaFacade zondaFacade, BinanceFacade binanceFacade) {

    @GetMapping(value = "/zonda")
    private String zonda(Model model) {
        model.addAttribute("exchangeRequest", new ExchangeRequest());
        model.addAttribute("spent", null);
        model.addAttribute("dates", this.zondaFacade.getYears());
        return "zonda";
    }

    @PostMapping(value = "/zonda")
    private String zonda(Model model, @Valid @ModelAttribute("exchangeRequest") ExchangeRequest exchangeRequest, BindingResult bindingResult) throws NoSuchAlgorithmException, IOException, InvalidKeyException, DocumentException, ParseException {
        model.addAttribute("spent", null);
        model.addAttribute("dates", this.zondaFacade.getYears());
        if (bindingResult.hasErrors()) {
            return "zonda";
        }
        model.addAttribute("userAction", exchangeRequest.getUserAction().getValue());
        model.addAttribute("spent", this.zondaFacade.getSpendings(exchangeRequest));
        return "zonda";
    }

    @GetMapping(value = "/binance")
    private String binance(Model model) {
        model.addAttribute("exchangeRequest", new ExchangeRequest());
        model.addAttribute("spent", null);
        model.addAttribute("dates", this.binanceFacade.getYears());
        return "binance";
    }

    @PostMapping(value = "/binance")
    private String binance(Model model, @Valid @ModelAttribute("exchangeRequest") ExchangeRequest exchangeRequest, BindingResult bindingResult) throws NoSuchAlgorithmException, IOException, InvalidKeyException, DocumentException, ParseException {
        model.addAttribute("spent", null);
        model.addAttribute("dates", this.binanceFacade.getYears());
        if (bindingResult.hasErrors()) {
            return "binance";
        }
        model.addAttribute("userAction", exchangeRequest.getUserAction().getValue());
        model.addAttribute("spent", this.binanceFacade.getSpendings(exchangeRequest));
        return "binance";
    }
}
