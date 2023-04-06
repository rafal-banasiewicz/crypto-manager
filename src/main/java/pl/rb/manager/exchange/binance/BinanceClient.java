package pl.rb.manager.exchange.binance;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.rb.manager.exchange.binance.model.BinanceResponse;
import pl.rb.manager.exchange.binance.model.BinanceServerTime;
import pl.rb.manager.exchange.utils.HmacProvider;
import pl.rb.manager.model.ExchangeRequest;
import pl.rb.manager.model.UserAction;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static pl.rb.manager.exchange.utils.DatesHelper.getBeginYearTimestamp;
import static pl.rb.manager.exchange.utils.DatesHelper.getEndYearTimestamp;

@Component
class BinanceClient {

    private final RestTemplate restTemplate;

    BinanceClient() {
        this.restTemplate = new RestTemplate();
    }

    public BinanceServerTime getBinanceServerTime() {
        return restTemplate.getForEntity("https://api.binance.com/api/v3/time", BinanceServerTime.class).getBody();
    }

    public BinanceResponse getBinanceResponse(ExchangeRequest exchangeRequest, BinanceServerTime binanceServerTime) throws NoSuchAlgorithmException, InvalidKeyException {
        var entity = new HttpEntity<>(createBinanceApiKeyHeader(exchangeRequest.getPublicKey()));
        return restTemplate.exchange(buildFiatPaymentUrl(exchangeRequest, binanceServerTime), HttpMethod.GET, entity, BinanceResponse.class).getBody();
    }

    private String buildFiatPaymentUrl(ExchangeRequest exchangeRequest, BinanceServerTime binanceServerTime) throws NoSuchAlgorithmException, InvalidKeyException {
        return buildFiatPaymentUrl(
                UserAction.getBinanceTransactionType(exchangeRequest.getUserAction()),
                getBeginYearTimestamp(exchangeRequest.getFromTime()),
                getEndYearTimestamp(exchangeRequest.getToTime()),
                binanceServerTime.getServerTime(),
                exchangeRequest.getPrivateKey()
        );
    }

    private String buildFiatPaymentUrl(String transactionType, long beginTime, long endTime, long timestamp, String privateKey) throws NoSuchAlgorithmException, InvalidKeyException {
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

    private HttpHeaders createBinanceApiKeyHeader(String publicKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MBX-APIKEY", publicKey);
        return headers;
    }
}
