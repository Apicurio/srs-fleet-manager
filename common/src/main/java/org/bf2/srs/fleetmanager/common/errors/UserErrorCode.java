package org.bf2.srs.fleetmanager.common.errors;

import lombok.Getter;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A list of error codes together with a template for user-friendly description (error reason).
 * <p>
 * IMPORTANT: These error codes are publicly exposed via REST.
 * Do not define internal errors, unless adding additional logic to prevent listing them.
 * Do not reuse error IDs.
 * Use only '%s' in the format string.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
public enum UserErrorCode {

    ERROR_UNKNOWN(1, "Unspecified error", 0),
    ERROR_REGISTRY_NOT_FOUND(2, "Registry with id='%s' not found", 1),
    ERROR_FORMAT_DATETIME(3, "Bad date or time format", 0),
    ERROR_FORMAT_REQUEST(4, "Invalid request content", 0),
    ERROR_FORMAT_REQUEST_JSON(5, "Bad request format - invalid JSON", 0),
    ERROR_REGISTRY_DEPLOYMENT_NOT_FOUND(6, "Registry Deployment with id='%s' not found", 1),
    ERROR_AMS_TERMS_NOT_ACCEPTED(7, "Required terms have not been accepted for account id='%s'", 1),
    ERROR_AMS_RESOURCE_LIMIT_REACHED(8, "The maximum number of allowed Registry instances has been reached", 0),
    ERROR_NOT_FOUND_ERROR_TYPE(9, "Error type with id='%s' not found", 1);

    public static final String ERROR_CODE_PREFIX = "SRSMGT-ERROR-";

    private static final SortedMap<Integer, UserErrorCode> MAP; // Natural Integer ordering

    static {
        SortedMap<Integer, UserErrorCode> map = new TreeMap<>(); // Natural Integer ordering
        for (UserErrorCode value : values()) {
            map.put(value.getId(), value);
        }
        MAP = Collections.unmodifiableSortedMap(map);
    }

    @Getter
    private final int id;

    @Getter
    private final String code;

    @Getter
    private final String reason;

    @Getter
    private final int reasonArgsCount;

    UserErrorCode(int id, String reason, int reasonArgsCount) {
        this.id = id;
        this.code = ERROR_CODE_PREFIX + id;
        this.reason = reason;
        this.reasonArgsCount = reasonArgsCount;
    }

    public static SortedMap<Integer, UserErrorCode> getValueMap() {
        return MAP;
    }
}
