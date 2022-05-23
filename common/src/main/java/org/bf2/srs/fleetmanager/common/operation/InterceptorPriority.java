package org.bf2.srs.fleetmanager.common.operation;

import javax.interceptor.Interceptor;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public interface InterceptorPriority {

    // ^^^ Caller

    int AUDITING = Interceptor.Priority.APPLICATION - 100; // = 1900

    int CHECK_READ_PERMISSIONS = Interceptor.Priority.APPLICATION - 99;

    int CHECK_DELETE_PERMISSIONS = Interceptor.Priority.APPLICATION - 98;

    int READ_ONLY_MODE = Interceptor.Priority.APPLICATION - 97;

    int LOGGING = Interceptor.Priority.APPLICATION - 96;

    // ~~~ Application Priority

    int RETRY_UNWRAP = 4000;

    // io.smallrye.faulttolerance.FaultToleranceInterceptor = 4010

    int RETRY_WRAP = 4020;

    // vvv Target
}
