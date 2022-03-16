package pl.rb.manager.zonda.helper;


import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class ZondaHelperFacade {

    private final IZondaDateCalculator zondaDateCalculator;
    private final IZondaHmacGenerator zondaHmacGenerator;

    public ZondaHelperFacade(IZondaDateCalculator zondaDateCalculator, IZondaHmacGenerator zondaHmacGenerator) {
        this.zondaDateCalculator = zondaDateCalculator;
        this.zondaHmacGenerator = zondaHmacGenerator;
    }

    public String getHmac(String algorithm, String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        return zondaHmacGenerator.getHmac(algorithm, data, key);
    }

    public String calculateFrom(String fromTime) {
        return zondaDateCalculator.calculateFrom(fromTime);
    }

    public String calculateTo(String toTime) {
        return zondaDateCalculator.calculateTo(toTime);
    }

}
