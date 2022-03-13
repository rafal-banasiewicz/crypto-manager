package pl.rb.manager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.springframework.stereotype.Service;
import pl.rb.manager.model.zonda.ZondaRequest;
import pl.rb.manager.model.zonda.ZondaResponse;
import pl.rb.manager.service.IZondaService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ZondaService implements IZondaService {

    public static final String HMAC_SHA512 = "HmacSHA512";
    public static final String START = "start";

    @Override
    public Double getSpendings(ZondaRequest zondaRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String nextPageCursor = START;
        String PUBLIC_KEY = zondaRequest.getPublicKey();
        long UNIX_TIME = Instant.now().getEpochSecond();
        UUID OPERATION_ID = UUID.randomUUID();
        String API_HASH = getHmac(HMAC_SHA512, PUBLIC_KEY + UNIX_TIME, zondaRequest.getPrivateKey());
        List<ZondaResponse> zondaResponses = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        while (true) {
            String params = URLEncoder.encode(getParams(zondaRequest, nextPageCursor), StandardCharsets.UTF_8);
            String URL = "https://api.zonda.exchange/rest/trading/history/transactions?query=" + params;
            Request request = buildRequest(PUBLIC_KEY, UNIX_TIME, OPERATION_ID, API_HASH, URL);
            ZondaResponse response = getZondaResponse(client.newCall(request).execute().body().string());
            if (response.getNextPageCursor().equals(nextPageCursor)) {
                break;
            } else {
                zondaResponses.add(response);
                nextPageCursor = response.getNextPageCursor();
            }
        }
        return getTotalSpent(zondaRequest, zondaResponses);
    }

    private static String getHmac(String algorithm, String data, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);
        return encodeHexString(mac.doFinal(data.getBytes()));
    }

    private static String encodeHexString(byte[] byteArray) {
        StringBuilder hexStringBuffer = new StringBuilder();
        for (byte b : byteArray) {
            hexStringBuffer.append(byteToHex(b));
        }
        return hexStringBuffer.toString();
    }

    private static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    private static String getParams(ZondaRequest zondaRequest, String nextPageCursor) {
        return "{\"fromTime\":\"" + calculateFrom(zondaRequest.getFromTime()) + "\", \"toTime\":\"" + calculateTo(zondaRequest.getToTime()) + "\", \"userAction\":\"" + zondaRequest.getUserAction() + "\", \"nextPageCursor\":\"" + nextPageCursor + "\"}";
    }

    private static String calculateFrom(String fromTime) {
        Instant instant = Instant.parse(fromTime + "-01-01T00:00:00.00Z");
        return String.valueOf(instant.getEpochSecond() * 1000);
    }

    private static String calculateTo(String fromTime) {
        Instant instant = Instant.parse(fromTime + "-12-31T23:59:59.99Z");
        return String.valueOf(instant.getEpochSecond() * 1000);
    }

    private static Request buildRequest(String PUBLIC_KEY, long UNIX_TIME, UUID OPERATION_ID, String API_HASH, String URL) {
        return new Request.Builder()
                .url(URL)
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("API-Key", PUBLIC_KEY)
                .addHeader("API-Hash", API_HASH)
                .addHeader("operation-id", String.valueOf(OPERATION_ID))
                .addHeader("Request-Timestamp", String.valueOf(UNIX_TIME))
                .build();
    }

    private static ZondaResponse getZondaResponse(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, ZondaResponse.class);
    }

    private static double getTotalSpent(ZondaRequest zondaRequest, List<ZondaResponse> zondaResponses) {
        return round(zondaResponses.stream()
                .flatMap(zondaResponse -> zondaResponse.getItems().stream()
                        .filter(zondaItem -> zondaItem.getMarket().substring(zondaItem.getMarket().lastIndexOf("-") + 1).length() == 3
                                && zondaItem.getMarket().substring(zondaItem.getMarket().lastIndexOf("-") + 1).contains(zondaRequest.getFiat())))
                .map(zondaItem -> Double.parseDouble(zondaItem.getAmount()) * Double.parseDouble(zondaItem.getRate()))
                .mapToDouble(Double::doubleValue).sum(), 2);
    }

    private static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
