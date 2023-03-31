package pl.rb.manager.zonda.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZondaQueryParams {

    String fromTime;
    String toTime;
    String userAction;
    String nextPageCursor;

    @Override
    public String toString() {
        return "{\"fromTime\":\"" + fromTime + "\"," + "\"toTime\":\"" + toTime + "\"," + "\"userAction\":\"" + userAction + "\"," + "\"nextPageCursor\":\"" + nextPageCursor + "\"}";
    }
}
