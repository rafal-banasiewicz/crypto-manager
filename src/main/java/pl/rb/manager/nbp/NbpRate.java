package pl.rb.manager.nbp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NbpRate {

    String no;
    String effectiveDate;
    BigDecimal mid;
}
