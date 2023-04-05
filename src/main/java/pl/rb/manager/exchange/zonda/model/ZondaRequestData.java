package pl.rb.manager.exchange.zonda.model;

import lombok.Builder;
import lombok.Data;
import pl.rb.manager.model.UserAction;

import java.util.UUID;

@Data
@Builder
public class ZondaRequestData {

    String nextPageCursor;
    String publicKey;
    long unixTime;
    UUID operationId;
    String apiHash;
    String fromTime;
    String toTime;
    UserAction userAction;
}
