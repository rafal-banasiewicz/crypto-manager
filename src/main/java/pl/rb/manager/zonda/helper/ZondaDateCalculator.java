package pl.rb.manager.zonda.helper;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
class ZondaDateCalculator {

    String calculateFrom(String fromTime) {
        var instant = Instant.parse(fromTime + "-01-01T00:00:00.00Z");
        return String.valueOf(instant.getEpochSecond() * 1000);
    }

    String calculateTo(String toTime) {
        var instant = Instant.parse(toTime + "-12-31T23:59:59.99Z");
        return String.valueOf(instant.getEpochSecond() * 1000);
    }
}
