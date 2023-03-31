package pl.rb.manager.zonda.helper;


import org.springframework.stereotype.Component;
import pl.rb.manager.zonda.model.ZondaRequestData;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public record ZondaHelperFacade(ZondaDateCalculator zondaDateCalculator, ZondaHmacProvider zondaHmacProvider) {

    public String getHmac(String algorithm, ZondaRequestData zondaRequestData) throws NoSuchAlgorithmException, InvalidKeyException {
        return zondaHmacProvider.getHmac(algorithm, zondaRequestData);
    }

    public String calculateFrom(String fromTime) {
        return zondaDateCalculator.calculateFrom(fromTime);
    }

    public String calculateTo(String toTime) {
        return zondaDateCalculator.calculateTo(toTime);
    }

}
