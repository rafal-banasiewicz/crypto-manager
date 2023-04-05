package pl.rb.manager.exchange.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import org.springframework.stereotype.Component;
import pl.rb.manager.model.Currency;
import pl.rb.manager.nbp.NbpRate;
import pl.rb.manager.nbp.NbpRequestBuilder;
import pl.rb.manager.nbp.NbpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class NbpHelper {

    private final OkHttpClient client;
    private final NbpRequestBuilder nbpRequestBuilder;
    private final ObjectMapper mapper;

    NbpHelper(OkHttpClient client, NbpRequestBuilder nbpRequestBuilder, ObjectMapper mapper) {
        this.client = client;
        this.nbpRequestBuilder = nbpRequestBuilder;
        this.mapper = mapper;
    }

    public List<NbpRate> getNbpRatesFromCorrespondingYears(String fromTime, String toTime, Currency currency) throws IOException {
        List<NbpRate> nbpRates = new ArrayList<>();
        int yearsCount = Integer.parseInt(toTime) - Integer.parseInt(fromTime) + 1;
        for(int i = 0; i < yearsCount; i++) {
            String requestYear = String.valueOf(Integer.parseInt(fromTime) + i);
            var request = nbpRequestBuilder.buildRequest(currency, requestYear);
            var response = mapper.readValue(client.newCall(request).execute().body().string(), NbpResponse.class);
            nbpRates.addAll(response.getRates());
        }
        return nbpRates;
    }
}
