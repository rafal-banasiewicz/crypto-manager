package pl.rb.manager.exchange.binance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceResponse {

    String code;
    String message;
    List<BinanceOrder> data;
    long total;
    boolean success;
}
