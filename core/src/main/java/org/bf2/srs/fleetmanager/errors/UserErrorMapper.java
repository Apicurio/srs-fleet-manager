package org.bf2.srs.fleetmanager.errors;

import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.validation.ConstraintViolationException;

import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;

public class UserErrorMapper {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(UserErrorMapper.class);

    private static final Map<Class<? extends Exception>, Function<Exception, UserErrorInfo>> MAP;

    static {

        Map<Class<? extends Exception>, Function<Exception, UserErrorInfo>> map = new HashMap<>();

        map.put(DateTimeParseException.class, ex -> UserErrorInfo.create(UserErrorCode.ERROR_FORMAT_DATETIME));
        map.put(ConstraintViolationException.class, ex -> UserErrorInfo.create(UserErrorCode.ERROR_FORMAT_REQUEST));
        map.put(JsonParseException.class, ex -> UserErrorInfo.create(UserErrorCode.ERROR_FORMAT_REQUEST_JSON));

        MAP = Collections.unmodifiableMap(map);
    }

    public static boolean hasMapping(Class<? extends Exception> key) {
        return MAP.containsKey(key);
    }

    public static UserErrorInfo getMapping(Exception ex) {
        if(!hasMapping(ex.getClass())) {
            throw new IllegalArgumentException("No mapping for exception", ex);
        }
        return MAP.get(ex.getClass()).apply(ex);
    }
}
