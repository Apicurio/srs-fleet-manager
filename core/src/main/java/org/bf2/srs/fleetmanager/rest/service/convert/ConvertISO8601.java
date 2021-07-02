package org.bf2.srs.fleetmanager.rest.service.convert;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ConvertISO8601 {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public String convert(@NotNull Instant instant) {
        return FORMATTER.format(instant);
    }

    public Instant convert(@NotNull String timestamp) {
        return FORMATTER.parse(timestamp, Instant::from);
    }
}
