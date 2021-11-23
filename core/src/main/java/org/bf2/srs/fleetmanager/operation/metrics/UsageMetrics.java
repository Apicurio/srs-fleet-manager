package org.bf2.srs.fleetmanager.operation.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.bf2.srs.fleetmanager.rest.service.RegistryService;
import org.bf2.srs.fleetmanager.rest.service.model.UsageStatisticsDto;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.bf2.srs.fleetmanager.operation.metrics.Constants.*;

@ApplicationScoped
public class UsageMetrics {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private static final Object DUMMY = new Object(); // Prevents NaN values on gauges, caused by garbage collection

    @Inject
    MeterRegistry metrics;

    @Inject
    RegistryService registryService;

    @Inject
    @ConfigProperty(name = "srs-fleet-manager.operation.metrics.usage-statistics.cache-expiration-period-seconds")
    Integer expirationPeriodSeconds;

    private Duration expirationPeriod;

    private Instant nextExpiration;

    private UsageStatisticsDto cached;

    public synchronized void init() {
        expirationPeriod = Duration.ofSeconds(expirationPeriodSeconds);

        int stagger = 0;
        // Only stagger if the expiration period is at least 1 minute (testing support).
        if (expirationPeriod.compareTo(Duration.ofMinutes(1)) >= 0) {
            stagger = new Random().nextInt(expirationPeriodSeconds) + 1;
            log.debug("Staggering usage metrics cache expiration by {} seconds", stagger);
        }
        nextExpiration = Instant.now().plus(Duration.ofSeconds(stagger));

        for (var entry : getUsageStatisticsCached().getRegistryCountPerStatus().entrySet()) {
            metrics.gauge(USAGE_STATISTICS_REGISTRIES, Tags.of(TAG_USAGE_STATISTICS_STATUS, entry.getKey().value()), DUMMY,
                    x -> getUsageStatisticsCached().getRegistryCountPerStatus().get(entry.getKey()));
        }

        metrics.gauge(USAGE_STATISTICS_ACTIVE_USERS, DUMMY,
                x -> getUsageStatisticsCached().getActiveUserCount());
        metrics.gauge(USAGE_STATISTICS_ACTIVE_ORGANISATIONS, DUMMY,
                x -> getUsageStatisticsCached().getActiveOrganisationCount());
    }

    public synchronized UsageStatisticsDto getUsageStatisticsCached() {
        boolean expired = Instant.now().isAfter(nextExpiration);
        if (cached == null || expired) {
            cached = registryService.getUsageStatistics();
            if (expired) {
                nextExpiration = Instant.now().plus(expirationPeriod);
            }
        }
        return cached;
    }
}
