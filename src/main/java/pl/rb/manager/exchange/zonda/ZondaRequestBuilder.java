package pl.rb.manager.exchange.zonda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Request;
import org.springframework.stereotype.Component;
import pl.rb.manager.exchange.zonda.model.ZondaQueryParams;
import pl.rb.manager.exchange.zonda.model.ZondaRequestData;
import pl.rb.manager.model.UserAction;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
record ZondaRequestBuilder(ZondaDateCalculator zondaDateCalculator, ObjectMapper mapper) {

    Request buildRequest(ZondaRequestData zondaRequestData) throws JsonProcessingException {
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

    private String getEncode(ZondaRequestData zondaRequestData) throws JsonProcessingException {
        return URLEncoder.encode(getParams(zondaRequestData), StandardCharsets.UTF_8);
    }

    private String getParams(ZondaRequestData zondaRequestData) throws JsonProcessingException {
    return mapper.writeValueAsString(ZondaQueryParams.builder()
            .fromTime(zondaDateCalculator.calculateFrom(zondaRequestData.getFromTime()))
            .toTime(zondaDateCalculator.calculateTo(zondaRequestData.getToTime()))
            .userAction(UserAction.getZondaUserAction(zondaRequestData.getUserAction()))
            .nextPageCursor(zondaRequestData.getNextPageCursor()).build());
    }

}
