package org.bf2.srs.fleetmanager.operation.readonly;

import org.bf2.srs.fleetmanager.App;
import org.bf2.srs.fleetmanager.common.operation.InterceptorPriority;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@ReadOnlySafeModeCheck
@Interceptor
@Priority(InterceptorPriority.READ_ONLY_MODE)
public class ReadOnlySafeModeInterceptor {

    @Inject
    App app;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        if (!app.isReadOnlySafeMode()) {
            return context.proceed();
        } else {
            throw new ReadOnlySafeModeException(context.getTarget().getClass(), context.getMethod());
        }
    }
}
