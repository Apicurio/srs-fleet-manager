package org.bf2.srs.fleetmanager.metrics;

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
}
