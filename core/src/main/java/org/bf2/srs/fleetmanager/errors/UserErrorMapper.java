package org.bf2.srs.fleetmanager.errors;

import com.fasterxml.jackson.core.JsonParseException;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.NotSupportedException;


/**
 * This mapper maps exceptions to user errors for cases where the underlying exception
 * has not been defined by us, and cannot implement {@link org.bf2.srs.fleetmanager.common.errors.UserError}.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class UserErrorMapper {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(UserErrorMapper.class);

    private static final Map<Class<? extends Exception>, Function<Exception, UserErrorInfo>> MAP;

    static {

        Map<Class<? extends Exception>, Function<Exception, UserErrorInfo>> map = new HashMap<>();

        map.put(DateTimeParseException.class, ex -> UserErrorInfo.create(UserErrorCode.ERROR_FORMAT_DATETIME));
        map.put(ConstraintViolationException.class, ex -> UserErrorInfo.create(UserErrorCode.ERROR_REQUEST_CONTENT_INVALID));
        map.put(JsonParseException.class, ex -> UserErrorInfo.create(UserErrorCode.ERROR_FORMAT_JSON));
        map.put(NotSupportedException.class, ex -> UserErrorInfo.create(UserErrorCode.ERROR_REQUEST_UNSUPPORTED_MEDIA_TYPE));

        MAP = Collections.unmodifiableMap(map);
    }

    public static boolean hasMapping(Class<? extends Exception> key) {
        return MAP.containsKey(key);
    }

    public static UserErrorInfo getMapping(Exception ex) {
        if (!hasMapping(ex.getClass())) {
            throw new IllegalArgumentException("No mapping for exception", ex);
        }
        return MAP.get(ex.getClass()).apply(ex);
    }
}
