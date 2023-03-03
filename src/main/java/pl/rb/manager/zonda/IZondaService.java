package pl.rb.manager.zonda;

import com.itextpdf.text.DocumentException;
import pl.rb.manager.zonda.model.ZondaRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

interface IZondaService {
    BigDecimal getSpendings(ZondaRequest zondaRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException, DocumentException;
}
