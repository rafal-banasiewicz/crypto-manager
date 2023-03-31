package pl.rb.manager.nbp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NbpResponse {

    String table;
    String currency;
    String code;
    List<NbpRate> rates;
}
