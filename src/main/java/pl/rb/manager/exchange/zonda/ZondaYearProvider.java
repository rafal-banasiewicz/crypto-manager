package pl.rb.manager.exchange.zonda;

import org.springframework.stereotype.Component;
import pl.rb.manager.exchange.YearProvider;

import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;

@Component
class ZondaYearProvider extends YearProvider {

    private static final int BITBAY_ZONDA_CREATE_YEAR = 2014;

    @Override
    protected List<Integer> getYears() {
        var currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return IntStream.rangeClosed(BITBAY_ZONDA_CREATE_YEAR, currentYear).boxed().toList();

    }
}
