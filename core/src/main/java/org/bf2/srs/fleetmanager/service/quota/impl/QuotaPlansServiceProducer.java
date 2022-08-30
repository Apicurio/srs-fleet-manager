package org.bf2.srs.fleetmanager.service.quota.impl;

import org.bf2.srs.fleetmanager.common.Current;
import org.bf2.srs.fleetmanager.service.quota.QuotaPlansService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
@ApplicationScoped
public class QuotaPlansServiceProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    FileQuotaPlansService fileQPS;

    @Inject
    LocalQuotaPlansService localQPS;

    @Produces
    @ApplicationScoped
    @Current
    QuotaPlansService produces() {
        if (fileQPS.isAvailable()) {
            log.info("Using FileQuotaPlansService implementation of QuotaPlansService");
            return fileQPS;
        } else {
            log.info("Using LocalQuotaPlansService implementation of QuotaPlansService");
            return localQPS;
        }
    }
}
