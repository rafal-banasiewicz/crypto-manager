package pl.rb.manager.zonda.model;

import java.util.Date;

public record ZondaPdfData(Date date, String userAction, String market, String amount, String rate, java.math.BigDecimal totalSpent) {
}
