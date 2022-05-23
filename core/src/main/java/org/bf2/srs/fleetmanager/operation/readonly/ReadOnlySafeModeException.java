package org.bf2.srs.fleetmanager.operation.readonly;

import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class ReadOnlySafeModeException extends IllegalStateException implements UserError {

    public ReadOnlySafeModeException() {
        super("Operation not available. Application is in a read-only safe mode");
    }

    public ReadOnlySafeModeException(Class<?> clazz, Method method) {
        super(String.format("Operation %s#%s(%s) not available. Application is in a read-only safe mode",
                Optional.ofNullable(clazz.getCanonicalName()).orElse("<unknown>"),
                method.getName(),
                Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).reduce((a, b) -> a + ", " + b)
        ));
    }

    @Override
    public UserErrorInfo getUserErrorInfo() {
        return UserErrorInfo.create(UserErrorCode.ERROR_MAINTENANCE);
    }
}
