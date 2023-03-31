package pl.rb.manager.zonda.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZondaResponse {

    private String status;
    private String totalRows;
    private List<ZondaItem> items;
    private Object query;
    private String nextPageCursor;
}
