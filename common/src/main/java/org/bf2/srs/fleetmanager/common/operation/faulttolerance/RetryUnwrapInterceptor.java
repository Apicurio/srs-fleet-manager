package org.bf2.srs.fleetmanager.common.operation.faulttolerance;

import org.bf2.srs.fleetmanager.common.operation.InterceptorPriority;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
@RetryUnwrap
@Interceptor
@Priority(InterceptorPriority.RETRY_UNWRAP)
public class RetryUnwrapInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        try {
            return context.proceed();
        } catch (RetryWrapperException ex) {
            throw ex.getWrapped();
        }
    }
}
