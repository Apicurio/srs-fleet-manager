package io.bf2fc6cc711aee1a0c2a.rest.convert;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import static java.util.Objects.requireNonNull;

@ApplicationScoped
public class ConvertISO8601 {

    private DateTimeFormatter formatter;

    @PostConstruct
    void init() {
        formatter = DateTimeFormatter.ISO_INSTANT;
    }

    public String convert(Instant instant) {
        requireNonNull(instant);
        return formatter.format(instant);
    }

    public Instant convert(String timeStamp) {
        requireNonNull(timeStamp);
        return ZonedDateTime.parse(timeStamp).toInstant();
    }
}
