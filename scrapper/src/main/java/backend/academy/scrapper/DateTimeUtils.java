package backend.academy.scrapper;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class DateTimeUtils {
    public static ZonedDateTime now() {
        return ZonedDateTime.now(ZoneId.of("UTC")).truncatedTo(ChronoUnit.MICROS);
    }
}
