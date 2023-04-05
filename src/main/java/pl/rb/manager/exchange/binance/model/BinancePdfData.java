package pl.rb.manager.exchange.binance.model;

public record BinancePdfData(String date, String status, String market, String amount, String rate, String fiatMultiplier, String fee, String feeInPLN, String spentAmount, String spentAmountInPln) {
}
