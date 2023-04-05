package pl.rb.manager.exchange.binance;

import com.squareup.okhttp.Request;
import org.springframework.stereotype.Component;
import pl.rb.manager.exchange.utils.HmacProvider;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
record BinanceRequestBuilder() {

    public Request buildPaymentsRequest(String transactionType, long beginTime, long endTime, long timestamp, String publicKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeyException {
        String value = String.format("transactionType=%s&beginTime=%s&endTime=%s&timestamp=%s", transactionType, beginTime, endTime, timestamp);
        String signature = HmacProvider.generateHmac("HmacSHA256", value, privateKey);
        return new Request.Builder()
                .url(String.format("https://api.binance.com/sapi/v1/fiat/payments?%s&signature=%s", value, signature))
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("X-MBX-APIKEY", publicKey)
                .build();
    }

    public Request buildServerTimeRequest() {
        return new Request.Builder()
                .url("https://api.binance.com/api/v3/time")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .get()
                .build();
    }
}
