package org.bf2.srs.fleetmanager;

import io.quarkus.scheduler.Scheduled;
import io.sentry.Sentry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * TODO: Remove me, or even better, move me to io.apicurio.common.apps.logging.sentry.AbstractSentryConfiguration
 * <p>
 * Emits a Sentry event on startup, to ensure the GlitchTip integration is working.
 */
@ApplicationScoped
public class TemporaryOnReadySentryEventEmitter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Scheduled(every = "P1000D", delayed = "45s")
    void onReady() {
        var message = "Sentry enabled for Fleet Manager";
        log.info("Trying to send a Sentry event: {}", message);
        Sentry.capture(message);
    }
}
