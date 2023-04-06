package pl.rb.manager.exchange.zonda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.rb.manager.exchange.utils.HmacProvider;
import pl.rb.manager.exchange.utils.NbpHelper;
import pl.rb.manager.exchange.zonda.model.ZondaOrder;
import pl.rb.manager.exchange.zonda.model.ZondaPdfData;
import pl.rb.manager.exchange.zonda.model.ZondaRequestData;
import pl.rb.manager.exchange.zonda.model.ZondaResponse;
import pl.rb.manager.model.Currency;
import pl.rb.manager.model.ExchangeRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static pl.rb.manager.exchange.utils.DatesHelper.formatDate;
import static pl.rb.manager.exchange.utils.DatesHelper.previousDayDate;

@Service
class ZondaService {

    private final ZondaHttpRequestBuilder zondaHttpRequestBuilder;
    private final ZondaPdfProvider zondaPdfProvider;
    private final NbpHelper nbpHelper;
    private final RestTemplate restTemplate;

    ZondaService(ZondaHttpRequestBuilder zondaHttpRequestBuilder, ZondaPdfProvider zondaPdfProvider, NbpHelper nbpHelper,
                 @Qualifier("zondaRestTemplate") RestTemplate restTemplate) {
        this.zondaHttpRequestBuilder = zondaHttpRequestBuilder;
        this.zondaPdfProvider = zondaPdfProvider;
        this.nbpHelper = nbpHelper;
        this.restTemplate = restTemplate;
    }


    String getSpendings(ExchangeRequest exchangeRequest) throws NoSuchAlgorithmException, InvalidKeyException, IOException, DocumentException, ParseException {
        var zondaResponses = getZondaResponses(exchangeRequest);
        var ordersBasedOnFiat = getOrdersBasedOnFiat(exchangeRequest.getFiat(), zondaResponses);
        if(isNotPLNCurrency(exchangeRequest)) {
            setFiatMultiplierForNonPLNCurrencies(exchangeRequest, ordersBasedOnFiat);
        }
        var totalSpent = getTotalSpentAmount(ordersBasedOnFiat);
        zondaPdfProvider.createDocument(createPdfData(ordersBasedOnFiat, exchangeRequest.getFiat()), totalSpent);
        return totalSpent;
    }

    private List<ZondaResponse> getZondaResponses(ExchangeRequest exchangeRequest) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        var zondaRequestData = getZondaRequestData(exchangeRequest);
        List<ZondaResponse> zondaResponses = new ArrayList<>();
        var entity = zondaHttpRequestBuilder.createHttpEntityWithHeaders(zondaRequestData);
        while (true) {
            var response = restTemplate.exchange(zondaHttpRequestBuilder.getTransactionHistoryUrl(), HttpMethod.GET, entity,
                    ZondaResponse.class, zondaHttpRequestBuilder.createQueryParams(zondaRequestData)).getBody();
            if (response.getNextPageCursor().equals(zondaRequestData.getNextPageCursor())) {
                break;
            }
            zondaResponses.add(response);
            zondaRequestData.setNextPageCursor(response.getNextPageCursor());
        }
        return zondaResponses;
    }

    private boolean isNotPLNCurrency(ExchangeRequest exchangeRequest) {
        return !exchangeRequest.getFiat().equals(Currency.PLN);
    }

    private void setFiatMultiplierForNonPLNCurrencies(ExchangeRequest exchangeRequest, List<ZondaOrder> ordersBasedOnFiat) throws IOException, ParseException {
        var nbpRates = nbpHelper.getNbpRatesFromCorrespondingYears(exchangeRequest.getFromTime(), exchangeRequest.getToTime(), exchangeRequest.getFiat());
        for (var order : ordersBasedOnFiat) {
            var transactionTime = order.getTime();
            while(true) {
                var previousDay = previousDayDate(transactionTime);
                var nbpRate = nbpRates.stream().filter(rate -> rate.getEffectiveDate().equals(previousDay)).findAny();
                if (nbpRate.isPresent()) {
                    order.setFiatMultiplier(nbpRate.get().getMid());
                    break;
                }
                transactionTime = previousDay;
            }
        }
    }

    private ZondaRequestData getZondaRequestData(ExchangeRequest exchangeRequest) throws NoSuchAlgorithmException, InvalidKeyException {
        var zondaRequestData = ZondaRequestData.builder()
                .nextPageCursor("start")
                .publicKey(exchangeRequest.getPublicKey())
                .unixTime(Instant.now().getEpochSecond())
                .operationId(UUID.randomUUID())
                .fromTime(exchangeRequest.getFromTime())
                .toTime(exchangeRequest.getToTime())
                .userAction(exchangeRequest.getUserAction()).build();
        zondaRequestData.setApiHash(HmacProvider.generateHmac("HmacSHA512", zondaRequestData.getPublicKey() + zondaRequestData.getUnixTime(), exchangeRequest.getPrivateKey()));
        return zondaRequestData;
    }


    private List<ZondaPdfData> createPdfData(List<ZondaOrder> ordersBasedOnFiat, Currency fiat) {
        return ordersBasedOnFiat
                .stream().map(order -> new ZondaPdfData(
                        order.getTime(),
                        order.getMarket(),
                        order.getAmount(),
                        order.getRate(),
                        fiat,
                        String.valueOf(order.getFiatMultiplier()),
                        String.format("%s %s", calculateSpentValue(order).setScale(2, RoundingMode.HALF_UP), fiat),
                        String.format("%s PLN", calculateSpentPLNValue(order).setScale(2, RoundingMode.HALF_UP))))
                .toList();
    }

    private String getTotalSpentAmount(List<ZondaOrder> zondaOrders) {
        return String.format("%s %s", zondaOrders
                .stream()
                .map(this::calculateSpentPLNValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP), Currency.PLN);
    }

    private BigDecimal calculateSpentValue(ZondaOrder zondaOrder) {
        var amount = new BigDecimal((zondaOrder.getAmount()));
        var rate = new BigDecimal(zondaOrder.getRate());
        return amount.multiply(rate);
    }

    private BigDecimal calculateSpentPLNValue(ZondaOrder zondaOrder) {
        var amount = new BigDecimal((zondaOrder.getAmount()));
        var rate = new BigDecimal(zondaOrder.getRate());
        var fiatMultiplier = zondaOrder.getFiatMultiplier();
        return amount.multiply(rate).multiply(fiatMultiplier);
    }

    private List<ZondaOrder> getOrdersBasedOnFiat(Currency fiat, List<ZondaResponse> zondaResponses) {
        return zondaResponses
                .stream()
                .flatMap(zondaResponse -> zondaResponse.getItems().stream())
                .filter(zondaOrder -> zondaOrder.getMarket().contains(String.valueOf(fiat)))
                .sorted(Comparator.comparing(ZondaOrder::getTime))
                .peek(order -> {
                    order.setTime(formatDate(Long.parseLong(order.getTime())));
                    order.setFiatMultiplier(new BigDecimal(1));
                })
                .toList();
    }
}
