package pl.rb.manager.zonda.helper;

import org.springframework.stereotype.Component;
import pl.rb.manager.zonda.model.ZondaRequestData;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
class ZondaHmacProvider {

    String getHmac(String algorithm, ZondaRequestData zondaRequestData) throws NoSuchAlgorithmException, InvalidKeyException {
        var secretKeySpec = new SecretKeySpec(zondaRequestData.getExchangeRequest().getPrivateKey().getBytes(), algorithm);
        var mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);
        var data = zondaRequestData.getPublicKey() + zondaRequestData.getUnixTime();
        return encodeHexString(mac.doFinal(data.getBytes()));
    }

    private String encodeHexString(byte[] byteArray) {
        var hexStringBuffer = new StringBuilder();
        for (var b : byteArray) {
            hexStringBuffer.append(byteToHex(b));
        }
        return hexStringBuffer.toString();
    }

    private String byteToHex(byte num) {
        var hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }
}
