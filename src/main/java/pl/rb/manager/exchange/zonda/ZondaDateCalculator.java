package pl.rb.manager.exchange.zonda;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
class ZondaDateCalculator {

    String calculateFrom(String year) {
        var instant = Instant.parse(String.format("%s-01-01T00:00:00.00Z", year));
        return String.valueOf(instant.getEpochSecond() * 1000);
    }

    String calculateTo(String year) {
        var instant = Instant.parse(String.format("%s-12-31T23:59:59.99Z", year));
        return String.valueOf(instant.getEpochSecond() * 1000);
    }
}
