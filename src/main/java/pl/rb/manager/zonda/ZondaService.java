package pl.rb.manager.zonda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.springframework.stereotype.Service;
import pl.rb.manager.zonda.helper.ZondaHelperFacade;
import pl.rb.manager.zonda.model.ZondaRequest;
import pl.rb.manager.zonda.model.ZondaResponse;

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
class ZondaService implements IZondaService {

    private static final String HMAC_SHA512 = "HmacSHA512";
    private static final String START = "start";

    private final ZondaHelperFacade zondaHelperFacade;

    ZondaService(ZondaHelperFacade zondaHelperFacade) {
        this.zondaHelperFacade = zondaHelperFacade;
    }

    @Override
    public Double getSpendings(ZondaRequest zondaRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String nextPageCursor = START;
        String PUBLIC_KEY = zondaRequest.getPublicKey();
        long UNIX_TIME = Instant.now().getEpochSecond();
        UUID OPERATION_ID = UUID.randomUUID();
        String API_HASH = zondaHelperFacade.getHmac(HMAC_SHA512, PUBLIC_KEY + UNIX_TIME, zondaRequest.getPrivateKey());
        List<ZondaResponse> zondaResponses = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        while (true) {
            String params = URLEncoder.encode(getParams(zondaRequest, nextPageCursor), StandardCharsets.UTF_8);
            String URL = "https://api.zonda.exchange/rest/trading/history/transactions?query=" + params;
            Request request = buildRequest(PUBLIC_KEY, UNIX_TIME, OPERATION_ID, API_HASH, URL);
            ZondaResponse response = getZondaResponse(client.newCall(request).execute().body().string());
            if (response.getNextPageCursor().equals(nextPageCursor)) {
                break;
            }
            zondaResponses.add(response);
            nextPageCursor = response.getNextPageCursor();
        }
        return getTotalSpent(zondaRequest, zondaResponses);
    }

    private String getParams(ZondaRequest zondaRequest, String nextPageCursor) {
        return "{\"fromTime\":\"" + zondaHelperFacade.calculateFrom(zondaRequest.getFromTime()) + "\", \"toTime\":\"" + zondaHelperFacade.calculateTo(zondaRequest.getToTime()) + "\", \"userAction\":\"" + zondaRequest.getUserAction() + "\", \"nextPageCursor\":\"" + nextPageCursor + "\"}";
    }

    private Request buildRequest(String PUBLIC_KEY, long UNIX_TIME, UUID OPERATION_ID, String API_HASH, String URL) {
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

    private ZondaResponse getZondaResponse(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, ZondaResponse.class);
    }

    private double getTotalSpent(ZondaRequest zondaRequest, List<ZondaResponse> zondaResponses) {
        return round(zondaResponses.stream()
                .flatMap(zondaResponse -> zondaResponse.getItems().stream()
                        .filter(zondaItem -> zondaItem.getMarket().substring(zondaItem.getMarket().lastIndexOf("-") + 1).length() == 3
                                && zondaItem.getMarket().substring(zondaItem.getMarket().lastIndexOf("-") + 1).contains(zondaRequest.getFiat())))
                .map(zondaItem -> Double.parseDouble(zondaItem.getAmount()) * Double.parseDouble(zondaItem.getRate()))
                .mapToDouble(Double::doubleValue).sum(), 2);
    }

    private double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
