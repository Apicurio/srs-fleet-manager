package io.bf2fc6cc711aee1a0c2a.rest.convert;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import javax.enterprise.context.ApplicationScoped;

import static java.util.Objects.requireNonNull;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ConvertISO8601 {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public String convert(Instant instant) {
        requireNonNull(instant);
        return FORMATTER.format(instant);
    }

    public Instant convert(String timestamp) {
        requireNonNull(timestamp);
        return FORMATTER.parse(timestamp, Instant::from);
    }
}
