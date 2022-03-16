package pl.rb.manager.zonda;

import pl.rb.manager.zonda.model.ZondaRequest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

interface IZondaService {
    Double getSpendings(ZondaRequest zondaRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException;
}
