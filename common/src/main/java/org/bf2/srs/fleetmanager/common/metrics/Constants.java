package org.bf2.srs.fleetmanager.common.metrics;

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

    // timers for dependencies on other services

    String AUTH_TIMER = PREFIX + "auth";
    String AUTH_TIMER_DESCRIPTION = "Timing and results of Auth layer";

    String AMS_TIMER_PREFIX = PREFIX + "ams.";
    String AMS_DETERMINE_ALLOWED_INSTANCE_TIMER = Constants.AMS_TIMER_PREFIX + "determine_allowed";
    String AMS_CREATE_TIMER = Constants.AMS_TIMER_PREFIX + "create";
    String AMS_DELETE_TIMER = Constants.AMS_TIMER_PREFIX + "delete";
    String AMS_TIMER_DESCRIPTION = "Timing and results of AMS client calls";

    String TENANT_MANAGER_PREFIX = PREFIX + "tm.";
    String TENANT_MANAGER_CREATE_TENANT_TIMER = TENANT_MANAGER_PREFIX + "create";
    String TENANT_MANAGER_DELETE_TENANT_TIMER = TENANT_MANAGER_PREFIX + "delete";
    String TENANT_MANAGER_DESCRIPTION = "Timing and results of tenant-manager client calls";

    String TAG_ERROR = "error";

    // REST API metrics

    String REST_PREFIX = "rest.";
    String REST_REQUESTS = REST_PREFIX + "requests";
    String REST_REQUESTS_TIMER_DESCRIPTION = "Timing and results of REST endpoints calls";

    String REST_REQUESTS_COUNTER = REST_REQUESTS + ".count";
    String REST_REQUESTS_COUNTER_DESCRIPTION = "Count and results of REST endpoints calls";

    // REST tags/labels

    String TAG_PATH = "path";
    String TAG_METHOD = "method";
    String TAG_STATUS_CODE_FAMILY = "status_code_group";
}
