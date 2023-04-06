package pl.rb.manager.exchange.binance;

import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import pl.rb.manager.exchange.binance.model.BinanceOrder;
import pl.rb.manager.exchange.binance.model.BinancePdfData;
import pl.rb.manager.exchange.binance.model.BinanceResponse;
import pl.rb.manager.exchange.binance.model.BinanceServerTime;
import pl.rb.manager.exchange.utils.NbpHelper;
import pl.rb.manager.model.Currency;
import pl.rb.manager.model.ExchangeRequest;
import pl.rb.manager.model.UserAction;
import pl.rb.manager.nbp.NbpRate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static pl.rb.manager.exchange.utils.DatesHelper.*;

@Service
class BinanceService {

    private final BinanceHttpRequestBuilder binanceHttpRequestBuilder;
    private final BinancePdfProvider binancePdfProvider;
    private final NbpHelper nbpHelper;
    private final RestTemplate restTemplate;

    BinanceService(BinanceHttpRequestBuilder binanceHttpRequestBuilder, BinancePdfProvider binancePdfProvider, NbpHelper nbpHelper,
                   @Qualifier("binanceRestTemplate") RestTemplate restTemplate) {
        this.binanceHttpRequestBuilder = binanceHttpRequestBuilder;
        this.binancePdfProvider = binancePdfProvider;
        this.nbpHelper = nbpHelper;
        this.restTemplate = restTemplate;
    }

    String getSpendings(ExchangeRequest exchangeRequest) throws IOException, NoSuchAlgorithmException, InvalidKeyException, DocumentException {
        var binanceServerTime = restTemplate.getForEntity(binanceHttpRequestBuilder.getServerTimeUrl(), BinanceServerTime.class);
        var entity = binanceHttpRequestBuilder.createHttpEntityWithHeader(exchangeRequest.getPublicKey());
        var fiatPaymentsUrl = getFiatPaymentsUrl(exchangeRequest, binanceServerTime);
        var binanceResponse = restTemplate.exchange(fiatPaymentsUrl, HttpMethod.GET, entity, BinanceResponse.class).getBody();
        if (!CollectionUtils.isEmpty((binanceResponse.getData()))) {
            binanceResponse.getData().forEach(order -> order.setTransactionTime(formatDate(order.getUpdateTime())));
            var ordersByCurrency = getOrdersByCurrency(binanceResponse);
            setFiatMultiplierForEachCurrency(exchangeRequest, ordersByCurrency);
            var accountOrders = ordersByCurrency.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .sorted(Comparator.comparing(BinanceOrder::getTransactionTime))
                    .toList();
            var totalSpent = getTotalSpentAmount(accountOrders);
            var feeSpent = getTotalFeeAmount(accountOrders);
            binancePdfProvider.createDocument(createPdfData(accountOrders), feeSpent, totalSpent);
            return totalSpent;
        }
        return "0";
    }

    private String getFiatPaymentsUrl(ExchangeRequest exchangeRequest, ResponseEntity<BinanceServerTime> binanceServerTime) throws NoSuchAlgorithmException, InvalidKeyException {
        return binanceHttpRequestBuilder.getFiatPaymentsUrl(
                UserAction.getBinanceTransactionType(exchangeRequest.getUserAction()),
                getBeginYearTimestamp(exchangeRequest.getFromTime()),
                getEndYearTimestamp(exchangeRequest.getToTime()),
                binanceServerTime.getBody().getServerTime(),
                exchangeRequest.getPrivateKey());
    }

    private Map<Currency, List<BinanceOrder>> getOrdersByCurrency(BinanceResponse binanceResponse) {
        return binanceResponse.getData().stream().collect(Collectors.groupingBy(BinanceOrder::getFiatCurrency));
    }

    private void setFiatMultiplierForEachCurrency(ExchangeRequest exchangeRequest, Map<Currency, List<BinanceOrder>> ordersByCurrency) {
        ordersByCurrency.forEach((currency, order) -> {
            switch (currency) {
                case PLN -> order.forEach(data -> data.setFiatMultiplier(new BigDecimal(1)));
                case EUR, USD -> {
                    try {
                        setFiatMultiplierForNonPLNCurrencies(exchangeRequest, order, currency);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private List<BinancePdfData> createPdfData(List<BinanceOrder> accountOrders) {
        return accountOrders
                .stream().map(order -> new BinancePdfData(
                        order.getTransactionTime(),
                        order.getStatus(),
                        order.getCryptoCurrency() + "-" + order.getFiatCurrency(),
                        order.getObtainAmount(),
                        order.getPrice(),
                        String.valueOf(order.getFiatMultiplier()),
                        String.format("%s %s", order.getTotalFee(), order.getFiatCurrency()),
                        String.format("%s PLN", calculateFeePLNValue(order).setScale(2, RoundingMode.HALF_UP)),
                        String.format("%s %s", calculateSpentValue(order).setScale(2, RoundingMode.HALF_UP), order.getFiatCurrency()),
                        String.format("%s PLN", calculateSpentPLNValue(order).setScale(2, RoundingMode.HALF_UP))))
                .toList();
    }

    private BigDecimal calculateSpentValue(BinanceOrder order) {
        if (!order.getStatus().equals("Completed")) {
            return new BigDecimal(0);
        }
        var amount = new BigDecimal((order.getObtainAmount()));
        var rate = new BigDecimal(order.getPrice());
        return amount.multiply(rate);
    }

    private String getTotalFeeAmount(List<BinanceOrder> orders) {
        return String.format("%s %s", orders
                .stream()
                .map(this::calculateFeePLNValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP), Currency.PLN);
    }

    private String getTotalSpentAmount(List<BinanceOrder> orders) {
        return String.format("%s %s", orders
                .stream()
                .map(this::calculateSpentPLNValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP), Currency.PLN);
    }

    private BigDecimal calculateSpentPLNValue(BinanceOrder order) {
        if (!order.getStatus().equals("Completed")) {
            return new BigDecimal(0);
        }
        var amount = new BigDecimal((order.getObtainAmount()));
        var rate = new BigDecimal(order.getPrice());
        var fiatMultiplier = order.getFiatMultiplier();
        return amount.multiply(rate).multiply(fiatMultiplier);
    }

    private BigDecimal calculateFeePLNValue(BinanceOrder order) {
        var fee = new BigDecimal((order.getTotalFee()));
        var fiatMultiplier = order.getFiatMultiplier();
        return fee.multiply(fiatMultiplier);
    }

    private void setFiatMultiplierForNonPLNCurrencies(ExchangeRequest exchangeRequest, List<BinanceOrder> accountOrders, Currency currency) throws IOException, ParseException {
        var nbpRates = nbpHelper.getNbpRatesFromCorrespondingYears(exchangeRequest.getFromTime(), exchangeRequest.getToTime(), currency);
        setAllOrdersFiatMultiplierFromDayBeforeTransaction(accountOrders, nbpRates);
    }

    private void setAllOrdersFiatMultiplierFromDayBeforeTransaction(List<BinanceOrder> accountOrders, List<NbpRate> nbpRates) throws ParseException {
        for (var order : accountOrders) {
            var transactionTime = order.getTransactionTime();
            while (true) {
                String previousDay = previousDayDate(transactionTime);
                Optional<NbpRate> nbpRate = nbpRates.stream().filter(rate -> rate.getEffectiveDate().equals(previousDay)).findAny();
                if (nbpRate.isPresent()) {
                    order.setFiatMultiplier(nbpRate.get().getMid());
                    break;
                }
                transactionTime = previousDay;
            }
        }
    }
}
