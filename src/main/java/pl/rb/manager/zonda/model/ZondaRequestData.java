package pl.rb.manager.zonda.model;

import lombok.Builder;
import lombok.Data;
import pl.rb.manager.model.ExchangeRequest;

import java.util.UUID;

@Data
@Builder
public class ZondaRequestData {

    String nextPageCursor;
    String publicKey;
    long unixTime;
    UUID operationId;
    String apiHash;
    ExchangeRequest exchangeRequest;
}
