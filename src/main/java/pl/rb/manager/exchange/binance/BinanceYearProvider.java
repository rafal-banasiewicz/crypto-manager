package pl.rb.manager.exchange.binance;

import org.springframework.stereotype.Component;
import pl.rb.manager.exchange.YearProvider;

import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;

@Component
public
class BinanceYearProvider extends YearProvider {

    private static final int BINANCE_CREATE_YEAR = 2017;

    @Override
    public List<Integer> getYears() {
        var currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return IntStream.rangeClosed(BINANCE_CREATE_YEAR, currentYear).boxed().toList();

    }
}