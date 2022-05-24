package org.bf2.srs.fleetmanager.common.operation.faulttolerance;

import org.bf2.srs.fleetmanager.common.operation.InterceptorPriority;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
@RetryWrap
@Interceptor
@Priority(InterceptorPriority.RETRY_WRAP)
public class RetryWrapInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        try {
            return context.proceed();
        } catch (Exception ex) {
            if (ex instanceof CanRetry) {
                CanRetry sr = (CanRetry) ex;
                if (sr.retry())
                    throw new RetryWrapperException(ex);
            }
            throw ex;
        }
    }
}
