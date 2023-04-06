package pl.rb.manager.exchange.binance;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.rb.manager.exchange.utils.HmacProvider;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
record BinanceHttpRequestBuilder() {

    @Bean(name = "binanceRestTemplate")
    public RestTemplate getBinanceRestTemplate() {
        return new RestTemplate();
    }

    public String getServerTimeUrl() {
        return UriComponentsBuilder.fromHttpUrl("https://api.binance.com/api/v3/time").toUriString();
    }

    public String getFiatPaymentsUrl(String transactionType, long beginTime, long endTime, long timestamp, String privateKey) throws NoSuchAlgorithmException, InvalidKeyException {
        String value = String.format("transactionType=%s&beginTime=%s&endTime=%s&timestamp=%s", transactionType, beginTime, endTime, timestamp);
        String signature = HmacProvider.generateHmac("HmacSHA256", value, privateKey);
        return UriComponentsBuilder.fromHttpUrl("https://api.binance.com/sapi/v1/fiat/payments")
                .queryParam("transactionType", transactionType)
                .queryParam("beginTime", beginTime)
                .queryParam("endTime", endTime)
                .queryParam("timestamp", timestamp)
                .queryParam("signature", signature)
                .encode()
                .toUriString();
    }

    public HttpEntity<String> createHttpEntityWithHeader(String publicKey) {
        return new HttpEntity<>(getRequiredHttpHeader(publicKey));
    }

    private HttpHeaders getRequiredHttpHeader(String publicKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MBX-APIKEY", publicKey);
        return headers;
    }
}
