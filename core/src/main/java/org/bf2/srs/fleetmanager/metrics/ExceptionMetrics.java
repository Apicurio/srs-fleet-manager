package org.bf2.srs.fleetmanager.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.bf2.srs.fleetmanager.spi.impl.exception.AccountManagementSystemClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ExceptionMetrics {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    MeterRegistry meterRegistry;

    public void record(Throwable t) {
        // Expose AMS errors as a metric
        if (t instanceof AccountManagementSystemClientException) {
            AccountManagementSystemClientException ex = (AccountManagementSystemClientException) t;
            log.debug("Recording metric for an AMS error.", ex);
            List<Tag> tags = new ArrayList<>(2);
            if (ex.getCauseEntity().isPresent()) {
                tags.add(Tag.of(Constants.TAG_AMS_CLIENT_ERROR_CODE, ex.getCauseEntity().get().getCode()));
            }
            if (ex.getStatusCode().isPresent()) {
                tags.add(Tag.of(Constants.TAG_AMS_CLIENT_STATUS_CODE, ex.getStatusCode().get().toString()));
            }
            meterRegistry.counter(Constants.AMS_CLIENT_ERRORS, tags).increment();
        }
    }
}
