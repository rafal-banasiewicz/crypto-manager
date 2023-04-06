package pl.rb.manager.exchange.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public final class HmacProvider {

    private HmacProvider() {
    }

    public static String generateHmac(String algorithm, String data, String privateKey) throws NoSuchAlgorithmException, InvalidKeyException {
        var secretKeySpec = new SecretKeySpec(privateKey.getBytes(), algorithm);
        var mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);
        return encodeHexString(mac.doFinal(data.getBytes()));
    }

    private static String encodeHexString(byte[] byteArray) {
        var hexStringBuffer = new StringBuilder();
        for (var b : byteArray) {
            hexStringBuffer.append(byteToHex(b));
        }
        return hexStringBuffer.toString();
    }

    private static String byteToHex(byte num) {
        var hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

}
