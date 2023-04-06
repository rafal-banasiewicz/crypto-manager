package pl.rb.manager.exchange.zonda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.rb.manager.exchange.zonda.model.ZondaQueryParams;
import pl.rb.manager.exchange.zonda.model.ZondaRequestData;
import pl.rb.manager.exchange.zonda.model.ZondaResponse;
import pl.rb.manager.model.UserAction;

@Component
class ZondaClient {

    private final ZondaDateCalculator zondaDateCalculator;
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    ZondaClient(ZondaDateCalculator zondaDateCalculator, ObjectMapper mapper) {
        this.zondaDateCalculator = zondaDateCalculator;
        this.mapper = mapper;
        this.restTemplate = new RestTemplate();
    }

    public ZondaResponse getZondaResponse(ZondaRequestData zondaRequestData) throws JsonProcessingException {
        var entity = createHttpEntityWithHeaders(zondaRequestData);
        return restTemplate.exchange(getTransactionHistoryUrl(), HttpMethod.GET, entity,
                ZondaResponse.class, createQueryParams(zondaRequestData)).getBody();
    }

    private String getTransactionHistoryUrl() {
        return UriComponentsBuilder.fromHttpUrl("https://api.zonda.exchange/rest/trading/history/transactions")
                .queryParam("query", "{query}")
                .encode()
                .toUriString();
    }

    private HttpEntity<String> createHttpEntityWithHeaders(ZondaRequestData zondaRequestData) {
        return new HttpEntity<>(getRequiredHttpHeaders(zondaRequestData));
    }

    private HttpHeaders getRequiredHttpHeaders(ZondaRequestData zondaRequestData) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("API-Key", zondaRequestData.getPublicKey());
        headers.set("API-Hash", zondaRequestData.getApiHash());
        headers.set("operation-id", String.valueOf(zondaRequestData.getOperationId()));
        headers.set("Request-Timestamp", String.valueOf(zondaRequestData.getUnixTime()));
        return headers;
    }

    private String createQueryParams(ZondaRequestData zondaRequestData) throws JsonProcessingException {
    return mapper.writeValueAsString(ZondaQueryParams.builder()
            .fromTime(zondaDateCalculator.calculateFrom(zondaRequestData.getFromTime()))
            .toTime(zondaDateCalculator.calculateTo(zondaRequestData.getToTime()))
            .userAction(UserAction.getZondaUserAction(zondaRequestData.getUserAction()))
            .nextPageCursor(zondaRequestData.getNextPageCursor()).build());
    }

}
