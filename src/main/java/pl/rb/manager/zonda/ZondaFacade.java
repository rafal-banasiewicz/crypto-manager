package pl.rb.manager.zonda;

import com.itextpdf.text.DocumentException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import pl.rb.manager.zonda.model.ZondaRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ZondaFacade {

    private static final int BITBAY_ZONDA_CREATE_YEAR = 2014;

    private final IZondaService zondaService;

    public ZondaFacade(IZondaService zondaService) {
        this.zondaService = zondaService;
    }

    public BigDecimal getSpendings(ZondaRequest zondaRequest) throws NoSuchAlgorithmException, IOException, InvalidKeyException, DocumentException {
        return zondaService.getSpendings(zondaRequest);
    }

    public void loadCommonZondaAttributes(Model model) {
        model.addAttribute("spent", null);
        model.addAttribute("dates", getYears());
    }

    private List<Integer> getYears() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return IntStream.rangeClosed(BITBAY_ZONDA_CREATE_YEAR, currentYear).boxed().toList();
    }

}
