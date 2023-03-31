package pl.rb.manager.nbp;

import com.squareup.okhttp.Request;
import org.springframework.stereotype.Component;
import pl.rb.manager.model.Currency;

@Component
public class NbpRequestBuilder {

    public Request buildRequest(Currency fiat, String year) {
        return new Request.Builder()
                .url("http://api.nbp.pl/api/exchangerates/rates/A/" + fiat + "/" + year + "-01-01" + "/" + year + "-12-31")
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();
    }
}
