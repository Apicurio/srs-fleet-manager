package org.bf2.srs.fleetmanager.operation.metrics;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public interface Constants {

    String PREFIX = "srs.fleet.manager.";

    /**
     * Counts errors returned by AMS.
     */
    String AMS_CLIENT_ERRORS = PREFIX + "ams.client.errors";
    /**
     * HTTP code returned by AMS.
     */
    String TAG_AMS_CLIENT_STATUS_CODE = "statusCode";
    /**
     * Error code returned in the Error entity by AMS.
     */
    String TAG_AMS_CLIENT_ERROR_CODE = "errorCode";

    String USAGE_STATISTICS_REGISTRIES_STATUS = PREFIX + "usage.registries.status";
    String USAGE_STATISTICS_REGISTRIES_TYPE = PREFIX + "usage.registries.type";
    String USAGE_STATISTICS_ACTIVE_USERS = PREFIX + "usage.users";
    String USAGE_STATISTICS_ACTIVE_ORGANISATIONS = PREFIX + "usage.organisations";

    String TAG_USAGE_STATISTICS_STATUS = "status";
    String TAG_USAGE_STATISTICS_TYPE = "type";
}
