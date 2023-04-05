package pl.rb.manager.exchange.binance;

import com.itextpdf.text.DocumentException;
import org.springframework.stereotype.Service;
import pl.rb.manager.model.ExchangeRequest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public record BinanceFacade(BinanceService binanceService, BinanceYearProvider binanceYearProvider) {

    public String getSpendings(ExchangeRequest exchangeRequest) throws IOException, NoSuchAlgorithmException, InvalidKeyException, DocumentException {
        return binanceService.getSpendings(exchangeRequest);
    }

    public List<Integer> getYears() {
        return binanceYearProvider.getYears();
    }

}