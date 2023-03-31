package pl.rb.manager.zonda;

import com.itextpdf.text.DocumentException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import pl.rb.manager.model.ExchangeRequest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;

@Service
public record ZondaFacade(ZondaService zondaService) {

    private static final int BITBAY_ZONDA_CREATE_YEAR = 2014;

    public String getSpendings(ExchangeRequest exchangeRequest) throws NoSuchAlgorithmException, IOException, InvalidKeyException, DocumentException, ParseException {
        return zondaService.getSpendings(exchangeRequest);
    }

    public void loadCommonZondaAttributes(Model model) {
        model.addAttribute("spent", null);
        model.addAttribute("dates", getYears());
    }

    private List<Integer> getYears() {
        var currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return IntStream.rangeClosed(BITBAY_ZONDA_CREATE_YEAR, currentYear).boxed().toList();
    }

}
