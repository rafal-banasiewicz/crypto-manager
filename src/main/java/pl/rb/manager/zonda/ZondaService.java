package pl.rb.manager.zonda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.squareup.okhttp.OkHttpClient;
import org.springframework.stereotype.Service;
import pl.rb.manager.model.ExchangeRequest;
import pl.rb.manager.nbp.NbpRate;
import pl.rb.manager.nbp.NbpRequestBuilder;
import pl.rb.manager.nbp.NbpResponse;
import pl.rb.manager.zonda.helper.ZondaHelperFacade;
import pl.rb.manager.zonda.model.*;
import pl.rb.manager.model.Currency;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
record ZondaService(ZondaHelperFacade zondaHelperFacade, ZondaPdfProvider zondaPdfProvider, ZondaRequestBuilder zondaRequestBuilder, NbpRequestBuilder nbpRequestBuilder) {

    String getSpendings(ExchangeRequest exchangeRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException, DocumentException, ParseException {
        var zondaRequestData = getZondaRequestData(exchangeRequest);
        List<ZondaResponse> zondaResponses = new ArrayList<>();
        var client = new OkHttpClient();
        while (true) {
            var request = zondaRequestBuilder.buildRequest(zondaRequestData);
            var response = getZondaResponse(client.newCall(request).execute().body().bytes());
            if (response.getNextPageCursor().equals(zondaRequestData.getNextPageCursor())) {
                break;
            }
            zondaResponses.add(response);
            zondaRequestData.setNextPageCursor(response.getNextPageCursor());
        }
        var itemsBasedOnFiat = getItemsBasedOnFiat(exchangeRequest.getFiat(), zondaResponses);
        if(isNotPLNCurrency(exchangeRequest)) {
            setFiatMultiplierForNonPLNCurrencies(exchangeRequest, client, itemsBasedOnFiat);
        }
        var totalSpent = getTotalSpentAmount(itemsBasedOnFiat, exchangeRequest.getFiat());
        zondaPdfProvider.createDocument(createPdfData(itemsBasedOnFiat, exchangeRequest.getFiat()), totalSpent);
        return totalSpent;
    }

    private boolean isNotPLNCurrency(ExchangeRequest exchangeRequest) {
        return !exchangeRequest.getFiat().equals(Currency.EUR);
    }

    private void setFiatMultiplierForNonPLNCurrencies(ExchangeRequest exchangeRequest, OkHttpClient client, List<ZondaItem> itemsBasedOnFiat) throws IOException, ParseException {
        List<NbpRate> nbpRates = new ArrayList<>();
        for(int i = 0; i < Integer.parseInt(exchangeRequest.getToTime()) - Integer.parseInt(exchangeRequest.getFromTime()) + 1; i++) {
            var request = nbpRequestBuilder.buildRequest(exchangeRequest.getFiat(), String.valueOf(Integer.parseInt(exchangeRequest.getFromTime()) + i));
            var response = getNbpResponse(client.newCall(request).execute().body().bytes());
            nbpRates.addAll(response.getRates());
        }
        for (var item : itemsBasedOnFiat) {
            var time = item.getTime();
            while(true) {
                String tempTime = previousDay(time);
                Optional<NbpRate> nbpRate = nbpRates.stream().filter(rate -> rate.getEffectiveDate().equals(tempTime)).findAny();
                if (nbpRate.isPresent()) {
                    item.setFiatMultiplier(nbpRate.get().getMid());
                    break;
                }
                time = tempTime;
            }
        }
    }

    private String previousDay(String time) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return df.format(calendar.getTime());
    }

    private ZondaRequestData getZondaRequestData(ExchangeRequest exchangeRequest) throws NoSuchAlgorithmException, InvalidKeyException {
        var zondaRequestData = ZondaRequestData.builder()
                .nextPageCursor("start")
                .publicKey(exchangeRequest.getPublicKey())
                .unixTime(Instant.now().getEpochSecond())
                .operationId(UUID.randomUUID())
                .exchangeRequest(exchangeRequest).build();
        zondaRequestData.setApiHash(zondaHelperFacade.getHmac("HmacSHA512", zondaRequestData));
        return zondaRequestData;
    }

    private ZondaResponse getZondaResponse(byte[] response) throws IOException {
        return new ObjectMapper().reader().readValue(response, ZondaResponse.class);
    }

    private NbpResponse getNbpResponse(byte[] response) throws IOException {
        return new ObjectMapper().reader().readValue(response, NbpResponse.class);
    }

    private List<ZondaPdfData> createPdfData(List<ZondaItem> itemsBasedOnFiat, Currency fiat) {
        return itemsBasedOnFiat
                .stream().map(item -> new ZondaPdfData(
                        item.getTime(),
                        item.getMarket(),
                        item.getAmount(),
                        item.getRate(),
                        fiat,
                        String.valueOf(item.getFiatMultiplier()),
                        String.format("%s %s", calculateSpentValue(item).setScale(2, RoundingMode.HALF_UP), fiat),
                        String.format("%s PLN", calculateSpentPLNValue(item).setScale(2, RoundingMode.HALF_UP))))
                .toList();
    }

    private String getTotalSpentAmount(List<ZondaItem> items, Currency fiat) {
        return String.format("%s %s", items
                .stream()
                .map(this::calculateSpentPLNValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP), fiat);
    }

    private BigDecimal calculateSpentValue(ZondaItem zondaItem) {
        var amount = new BigDecimal((zondaItem.getAmount()));
        var rate = new BigDecimal(zondaItem.getRate());
        return amount.multiply(rate);
    }

    private BigDecimal calculateSpentPLNValue(ZondaItem zondaItem) {
        var amount = new BigDecimal((zondaItem.getAmount()));
        var rate = new BigDecimal(zondaItem.getRate());
        var fiatMultiplier = zondaItem.getFiatMultiplier();
        return amount.multiply(rate).multiply(fiatMultiplier);
    }

    private List<ZondaItem> getItemsBasedOnFiat(Currency fiat, List<ZondaResponse> zondaResponses) {
        var zondaItems = zondaResponses
                .stream()
                .flatMap(zondaResponse -> zondaResponse.getItems().stream())
                .filter(zondaItem -> zondaItem.getMarket().contains(String.valueOf(fiat)))
                .sorted(Comparator.comparing(ZondaItem::getTime))
                .toList();
        var df = new SimpleDateFormat("yyyy-MM-dd");
        zondaItems.forEach(item -> {
            item.setTime(df.format(new Date(Long.parseLong(item.getTime()))));
            item.setFiatMultiplier(new BigDecimal(1));
        });
        return zondaItems;
    }
}
