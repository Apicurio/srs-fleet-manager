package org.bf2.srs.fleetmanager.common.errors;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import lombok.Getter;
import lombok.ToString;

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
@ToString
public enum UserErrorCode {

    ERROR_UNKNOWN(1, "Unspecified error", 0),

    ERROR_REGISTRY_NOT_FOUND(2, "Registry with id='%s' not found", 1),
    ERROR_REGISTRY_DATA_CONFLICT(9, "Data conflict. Make sure a Registry with the given name does not already exist", 0),

    ERROR_FORMAT_DATETIME(3, "Bad date or time format", 0),
    ERROR_FORMAT_JSON(5, "Bad request format - invalid JSON", 0),

    ERROR_REQUEST_CONTENT_INVALID(4, "Invalid request content. Make sure the request conforms to the given JSON schema", 0),
    ERROR_REQUEST_UNSUPPORTED_MEDIA_TYPE(10, "Bad request format - unsupported media type", 0),

    ERROR_AMS_TERMS_NOT_ACCEPTED(6, "Required terms have not been accepted for account id='%s'", 1),
    ERROR_AMS_RESOURCE_LIMIT_REACHED(7, "The maximum number of allowed Registry instances has been reached", 0),
    ERROR_AMS_FAILED_TO_CHECK_QUOTA(11, "Could not check quota for user%s", 1),

    ERROR_ERROR_TYPE_NOT_FOUND(8, "Error type with id='%s' not found", 1),

    ERROR_EVAL_INSTANCES_NOT_ALLOWED(12, "Evaluation instances not allowed.", 0),
    ERROR_EVAL_INSTANCES_EXCEEDED(13, "User already has the maximum number of allowed Evaluation instances.", 0),
    ERROR_TOO_MANY_INSTANCES(14, "Total (global) number of instances exhausted.", 0),
    ;

    // Next ID: 15

    public static final String ERROR_CODE_PREFIX = "SRS-MGMT-";

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
