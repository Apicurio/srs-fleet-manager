package org.bf2.srs.fleetmanager.common.operation.faulttolerance;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@RetryUnwrap
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class RetryUnwrapInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        try {
            return context.proceed();
        } catch (Exception ex) {
            if (ex instanceof RetryWrapperException) {
                throw ((RetryWrapperException) ex).getWrapped();
            } else {
                throw ex;
            }
        }
    }
}
