package pl.rb.manager.exchange.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.rb.manager.model.Currency;
import pl.rb.manager.nbp.NbpRate;
import pl.rb.manager.nbp.NbpResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NbpHelper {

    public List<NbpRate> getNbpRatesFromCorrespondingYears(String fromTime, String toTime, Currency currency) {
        List<NbpRate> nbpRates = new ArrayList<>();
        int yearsCount = Integer.parseInt(toTime) - Integer.parseInt(fromTime) + 1;
        var restTemplate = new RestTemplate();
        for (int i = 0; i < yearsCount; i++) {
            String requestYear = String.valueOf(Integer.parseInt(fromTime) + i);
            var response = restTemplate.getForEntity(getExchangeRatesUrl(currency, requestYear), NbpResponse.class);
            nbpRates.addAll(response.getBody().getRates());
        }
        return nbpRates;
    }

    private String getExchangeRatesUrl(Currency currency, String year) {
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("table", "A");
        urlParams.put("fiat", currency.toString());
        urlParams.put("startDate", year + "-01-01");
        urlParams.put("endDate", year + "-12-31");
        return UriComponentsBuilder.fromUriString("http://api.nbp.pl/api/exchangerates/rates/{table}/{fiat}/{startDate}/{endDate}").buildAndExpand(urlParams).toUriString();
    }
}
