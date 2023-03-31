package pl.rb.manager.zonda.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZondaItem {

    String id;
    String market;
    String time;
    String amount;
    String rate;
    String initializedBy;
    String wasTaker;
    String userAction;
    String offerId;
    String commissionValue;
    BigDecimal fiatMultiplier;
}
