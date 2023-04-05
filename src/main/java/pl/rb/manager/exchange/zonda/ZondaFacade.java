package pl.rb.manager.exchange.zonda;

import com.itextpdf.text.DocumentException;
import org.springframework.stereotype.Service;
import pl.rb.manager.model.ExchangeRequest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;

@Service
public record ZondaFacade(ZondaService zondaService, ZondaYearProvider zondaYearProvider) {

    public String getSpendings(ExchangeRequest exchangeRequest) throws NoSuchAlgorithmException, IOException, InvalidKeyException, DocumentException, ParseException {
        return zondaService.getSpendings(exchangeRequest);
    }

    public List<Integer> getYears() {
        return zondaYearProvider.getYears();
    }

}
