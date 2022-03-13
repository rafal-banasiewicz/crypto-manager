package pl.rb.manager.service.impl;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.springframework.stereotype.Service;
import pl.rb.manager.model.zonda.ZondaRequest;
import pl.rb.manager.model.zonda.ZondaResponse;
import pl.rb.manager.service.IZondaService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pl.rb.manager.utils.Const.HMAC_SHA512;
import static pl.rb.manager.utils.Const.START;
import static pl.rb.manager.utils.ZondaHelper.*;

@Service
public class ZondaService implements IZondaService {

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
}
