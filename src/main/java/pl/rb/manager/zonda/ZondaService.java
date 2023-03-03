package pl.rb.manager.zonda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.springframework.stereotype.Service;
import pl.rb.manager.zonda.helper.ZondaHelperFacade;
import pl.rb.manager.zonda.model.ZondaItem;
import pl.rb.manager.zonda.model.ZondaPdfData;
import pl.rb.manager.zonda.model.ZondaRequest;
import pl.rb.manager.zonda.model.ZondaResponse;

import javax.swing.filechooser.FileSystemView;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
class ZondaService implements IZondaService {

    private static final String HMAC_SHA512 = "HmacSHA512";
    private static final String START = "start";

    private final ZondaHelperFacade zondaHelperFacade;

    ZondaService(ZondaHelperFacade zondaHelperFacade) {
        this.zondaHelperFacade = zondaHelperFacade;
    }

    @Override
    public BigDecimal getSpendings(ZondaRequest zondaRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException, DocumentException {
        String nextPageCursor = START;
        String PUBLIC_KEY = zondaRequest.getPublicKey();
        long UNIX_TIME = Instant.now().getEpochSecond();
        UUID OPERATION_ID = UUID.randomUUID();
        String API_HASH = zondaHelperFacade.getHmac(HMAC_SHA512, PUBLIC_KEY + UNIX_TIME, zondaRequest.getPrivateKey());
        List<ZondaResponse> zondaResponses = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        while (true) {
            String params = URLEncoder.encode(getParams(zondaRequest, nextPageCursor), StandardCharsets.UTF_8);
            String URL = "https://api.zonda.exchange/rest/trading/history/transactions?query=" + params;
            Request request = buildRequest(PUBLIC_KEY, UNIX_TIME, OPERATION_ID, API_HASH, URL);
            ZondaResponse response = getZondaResponse(client.newCall(request).execute().body().string());
            if (response.getNextPageCursor().equals(nextPageCursor)) {
                break;
            }
            zondaResponses.add(response);
            nextPageCursor = response.getNextPageCursor();
        }
        List<ZondaItem> itemsBasedOnFiat = getItemsBasedOnFiat(zondaRequest.getFiat(), zondaResponses);
        List<ZondaPdfData> pdfData = itemsBasedOnFiat.stream().map(item -> new ZondaPdfData(new Date(Long.parseLong(item.getTime())), item.getUserAction(),
                item.getMarket(), item.getAmount(), item.getRate(), new BigDecimal((item.getAmount())).multiply(new BigDecimal(item.getRate())).setScale(2, RoundingMode.HALF_UP))).toList();
        BigDecimal totalSpent = getTotalSpent(itemsBasedOnFiat);
        getDocument(pdfData, totalSpent);
        return totalSpent;
    }

    private void getDocument(List<ZondaPdfData> pdfData, BigDecimal totalSpent) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(FileSystemView.getFileSystemView().getHomeDirectory() + "/MoneySpent.pdf"));
        document.open();
        PdfPTable table = new PdfPTable(6);
        addTableHeader(table);
        addRows(table, pdfData, String.valueOf(totalSpent));
        document.add(table);
        document.close();
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Date", "Action", "Market", "Amount", "Rate", "Spent")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, List<ZondaPdfData> pdfData, String totalSpent) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (ZondaPdfData data : pdfData) {
            table.addCell(df.format(data.date()));
            table.addCell(data.userAction());
            table.addCell(data.market());
            table.addCell(data.amount());
            table.addCell(data.rate());
            table.addCell(String.valueOf(data.totalSpent()));
        }
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("Total Spent:");
        table.addCell(totalSpent);
    }

    private String getParams(ZondaRequest zondaRequest, String nextPageCursor) {
        return "{\"fromTime\":\"" + zondaHelperFacade.calculateFrom(zondaRequest.getFromTime()) + "\", \"toTime\":\"" + zondaHelperFacade.calculateTo(zondaRequest.getToTime()) + "\", \"userAction\":\"" + zondaRequest.getUserAction() + "\", \"nextPageCursor\":\"" + nextPageCursor + "\"}";
    }

    private Request buildRequest(String PUBLIC_KEY, long UNIX_TIME, UUID OPERATION_ID, String API_HASH, String URL) {
        return new Request.Builder()
                .url(URL)
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("API-Key", PUBLIC_KEY)
                .addHeader("API-Hash", API_HASH)
                .addHeader("operation-id", String.valueOf(OPERATION_ID))
                .addHeader("Request-Timestamp", String.valueOf(UNIX_TIME))
                .build();
    }

    private ZondaResponse getZondaResponse(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, ZondaResponse.class);
    }

    private BigDecimal getTotalSpent(List<ZondaItem> items) {
        return items
                .stream().map(zondaItem -> new BigDecimal((zondaItem.getAmount())).multiply(new BigDecimal(zondaItem.getRate())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    private List<ZondaItem> getItemsBasedOnFiat(String fiat, List<ZondaResponse> zondaResponses) {
        return zondaResponses.stream().flatMap(zondaResponse -> zondaResponse.getItems().stream().filter(zondaItem -> zondaItem.getMarket().contains(fiat))).toList();
    }
}
