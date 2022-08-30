package org.bf2.srs.fleetmanager.service.quota;

import org.bf2.srs.fleetmanager.service.quota.model.QuotaPlan;

public interface QuotaPlansService {

    /**
     * Initialize and run the service when the application starts
     */
    void start() throws Exception;

    /**
     * Determine correct quota plan based on provided information
     */
    QuotaPlan determineQuotaPlan(String orgId);
}
