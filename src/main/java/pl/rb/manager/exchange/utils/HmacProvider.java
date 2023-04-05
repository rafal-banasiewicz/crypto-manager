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

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
        //GET
        //https://api.binance.com/api/v3/time

        //GET
        //https://api.binance.com/sapi/v1/fiat/payments?
        // transactionType=0&
        // timestamp=1680381620395&
        // signature=c9d8312e3783650b45ccc17598426665ee837aa2d1d735426ec69c440fc9fd84

        String hmac = generateHmac("HmacSHA256", "transactionType=0&beginTime=1609462800000&recvWindow=60000&timestamp=1680436320737", "ijrGC5Fd7bmc1AwZ9Nhd012IDKMgSEe916SBIOJHRFqoCjJIomg5UYliz3mqRKOt"); //secretKey
        System.out.println("https://api.binance.com/sapi/v1/fiat/payments?transactionType=0&beginTime=1609462800000&recvWindow=60000&timestamp=1680436320737&signature=" + hmac);
    }

}
