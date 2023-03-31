package pl.rb.manager.zonda.model;

import pl.rb.manager.model.Currency;

public record ZondaPdfData(String date, String market, String amount, String rate, Currency fiat, String fiatMultiplier, String spentAmount, String spentAmountInPln) {
}
