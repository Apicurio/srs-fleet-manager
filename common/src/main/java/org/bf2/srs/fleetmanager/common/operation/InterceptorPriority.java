package org.bf2.srs.fleetmanager.common.operation;

import javax.interceptor.Interceptor;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public class InterceptorPriority {

    private InterceptorPriority() {
    }

    // ^^^ Caller

    public static final int AUDITING = Interceptor.Priority.APPLICATION - 100; // = 1900

    public static final int CHECK_READ_PERMISSIONS = Interceptor.Priority.APPLICATION - 99;

    public static final int CHECK_DELETE_PERMISSIONS = Interceptor.Priority.APPLICATION - 98;

    public static final int READ_ONLY_MODE = Interceptor.Priority.APPLICATION - 97;

    public static final int LOGGING = Interceptor.Priority.APPLICATION - 96;

    // ~~~ Application Priority

    public static final int RETRY_UNWRAP = 4000;

    // io.smallrye.faulttolerance.FaultToleranceInterceptor = 4010

    public static final int RETRY_WRAP = 4020;

    // vvv Target
}
