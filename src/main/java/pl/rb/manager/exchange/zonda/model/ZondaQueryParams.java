package pl.rb.manager.exchange.zonda.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZondaQueryParams {

    String fromTime;
    String toTime;
    String userAction;
    String nextPageCursor;

}
