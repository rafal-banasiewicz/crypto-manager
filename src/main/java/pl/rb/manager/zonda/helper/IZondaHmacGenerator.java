package pl.rb.manager.zonda.helper;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

interface IZondaHmacGenerator {
    String getHmac(String algorithm, String data, String key) throws NoSuchAlgorithmException, InvalidKeyException;
}
