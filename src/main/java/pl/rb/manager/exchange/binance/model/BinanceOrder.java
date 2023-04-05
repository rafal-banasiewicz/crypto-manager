package pl.rb.manager.exchange.binance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.rb.manager.model.Currency;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceOrder {

    String orderNo;
    String sourceAmount;
    Currency fiatCurrency;
    String obtainAmount;
    String cryptoCurrency;
    String totalFee;
    String price;
    String status;
    String paymentMethod;
    long createTime;
    long updateTime;
    BigDecimal fiatMultiplier;
    String transactionTime;
}
