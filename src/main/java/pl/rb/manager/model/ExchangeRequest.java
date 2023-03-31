package pl.rb.manager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequest {

    Currency fiat;
    String fromTime;
    String toTime;
    String userAction;
    @NotBlank(message = " cannot be empty!")
    String publicKey;
    @NotBlank(message = " cannot be empty!")
    String privateKey;
}
