package pl.rb.manager.zonda;

import java.util.List;

class ZondaResponse {

    private String status;
    private String totalRows;
    private List<ZondaItem> items;
    private Object query;
    private String nextPageCursor;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(String totalRows) {
        this.totalRows = totalRows;
    }

    public List<ZondaItem> getItems() {
        return items;
    }

    public void setItems(List<ZondaItem> items) {
        this.items = items;
    }

    public Object getQuery() {
        return query;
    }

    public void setQuery(Object query) {
        this.query = query;
    }

    public String getNextPageCursor() {
        return nextPageCursor;
    }

    public void setNextPageCursor(String nextPageCursor) {
        this.nextPageCursor = nextPageCursor;
    }

    @Override
    public String toString() {
        return "ZondaResponse{" +
                "status='" + status + '\'' +
                ", totalRows='" + totalRows + '\'' +
                ", items=" + items +
                ", query=" + query +
                ", nextPageCursor='" + nextPageCursor + '\'' +
                '}';
    }
}
