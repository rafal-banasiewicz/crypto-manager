package pl.rb.manager.zonda;

import com.squareup.okhttp.Request;
import org.springframework.stereotype.Component;
import pl.rb.manager.zonda.helper.ZondaHelperFacade;
import pl.rb.manager.zonda.model.ZondaQueryParams;
import pl.rb.manager.zonda.model.ZondaRequestData;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
record ZondaRequestBuilder(ZondaHelperFacade zondaHelperFacade) {

    Request buildRequest(ZondaRequestData zondaRequestData) {
        return new Request.Builder()
                .url("https://api.zonda.exchange/rest/trading/history/transactions?query=" + getEncode(zondaRequestData))
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("API-Key", zondaRequestData.getPublicKey())
                .addHeader("API-Hash", zondaRequestData.getApiHash())
                .addHeader("operation-id", String.valueOf(zondaRequestData.getOperationId()))
                .addHeader("Request-Timestamp", String.valueOf(zondaRequestData.getUnixTime()))
                .build();
    }

    private String getEncode(ZondaRequestData zondaRequestData) {
        return URLEncoder.encode(getParams(zondaRequestData), StandardCharsets.UTF_8);
    }

    private String getParams(ZondaRequestData zondaRequestData) {
    return ZondaQueryParams.builder()
            .fromTime(zondaHelperFacade.calculateFrom(zondaRequestData.getExchangeRequest().getFromTime()))
            .toTime(zondaHelperFacade.calculateTo(zondaRequestData.getExchangeRequest().getToTime()))
            .userAction(zondaRequestData.getExchangeRequest().getUserAction())
            .nextPageCursor(zondaRequestData.getNextPageCursor()).build().toString();
    }

}
