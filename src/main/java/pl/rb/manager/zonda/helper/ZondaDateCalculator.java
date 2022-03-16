package pl.rb.manager.zonda.helper;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
class ZondaDateCalculator implements IZondaDateCalculator {

    @Override
    public String calculateFrom(String fromTime) {
        Instant instant = Instant.parse(fromTime + "-01-01T00:00:00.00Z");
        return String.valueOf(instant.getEpochSecond() * 1000);
    }

    @Override
    public String calculateTo(String toTime) {
        Instant instant = Instant.parse(toTime + "-12-31T23:59:59.99Z");
        return String.valueOf(instant.getEpochSecond() * 1000);
    }
}
