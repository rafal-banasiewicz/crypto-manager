package pl.rb.manager.service;

import pl.rb.manager.model.zonda.ZondaRequest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface IZondaService {
    Double getSpendings(ZondaRequest zondaRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException;
}
