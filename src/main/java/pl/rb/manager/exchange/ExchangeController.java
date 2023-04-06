package pl.rb.manager.exchange;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.rb.manager.exchange.binance.BinanceService;
import pl.rb.manager.exchange.binance.BinanceYearProvider;
import pl.rb.manager.exchange.zonda.ZondaService;
import pl.rb.manager.exchange.zonda.ZondaYearProvider;
import pl.rb.manager.model.ExchangeRequest;

import javax.validation.Valid;

@Controller
record ExchangeController(ZondaService zondaService, ZondaYearProvider zondaYearProvider, BinanceService binanceService, BinanceYearProvider binanceYearProvider) {

    @GetMapping(value = "/zonda")
    private String zonda(Model model) {
        model.addAttribute("exchangeRequest", new ExchangeRequest());
        model.addAttribute("spent", null);
        model.addAttribute("dates", this.zondaYearProvider.getYears());
        return "zonda";
    }

    @PostMapping(value = "/zonda")
    private String zonda(Model model, @Valid @ModelAttribute("exchangeRequest") ExchangeRequest exchangeRequest, BindingResult bindingResult) throws Exception {
        model.addAttribute("spent", null);
        model.addAttribute("dates", this.zondaYearProvider.getYears());
        if (bindingResult.hasErrors()) {
            return "zonda";
        }
        model.addAttribute("userAction", exchangeRequest.getUserAction().getValue());
        model.addAttribute("spent", this.zondaService.getSpendings(exchangeRequest));
        return "zonda";
    }

    @GetMapping(value = "/binance")
    private String binance(Model model) {
        model.addAttribute("exchangeRequest", new ExchangeRequest());
        model.addAttribute("spent", null);
        model.addAttribute("dates", this.binanceYearProvider.getYears());
        return "binance";
    }

    @PostMapping(value = "/binance")
    private String binance(Model model, @Valid @ModelAttribute("exchangeRequest") ExchangeRequest exchangeRequest, BindingResult bindingResult) throws Exception {
        model.addAttribute("spent", null);
        model.addAttribute("dates", this.binanceYearProvider.getYears());
        if (bindingResult.hasErrors()) {
            return "binance";
        }
        model.addAttribute("userAction", exchangeRequest.getUserAction().getValue());
        model.addAttribute("spent", this.binanceService.getSpendings(exchangeRequest));
        return "binance";
    }
}
