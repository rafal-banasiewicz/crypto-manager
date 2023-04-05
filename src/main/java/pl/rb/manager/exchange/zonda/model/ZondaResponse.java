package pl.rb.manager.exchange.zonda.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZondaResponse {

    private String status;
    private String totalRows;
    private List<ZondaOrder> items;
    private Object query;
    private String nextPageCursor;
}
