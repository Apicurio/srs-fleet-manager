package org.bf2.srs.fleetmanager.ams;

import lombok.Getter;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.FaultToleranceConstants;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.RetryUnwrap;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.RetryWrapperException;
import org.bf2.srs.fleetmanager.spi.impl.exception.AccountManagementSystemClientException;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RetryMock {

    @Getter
    private int counter = 0;

    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @RetryUnwrap
    @Retry(retryOn = {RetryWrapperException.class}) // 3 retries, 200ms jitter
    public String runRetriesWithTimeout(int failedAttempts, int millis) throws Exception {
        Thread.sleep(millis);
        counter++;
        if (counter <= failedAttempts) {
            var ex = new AccountManagementSystemClientException("Message", 500);
            throw ex.convert();
        } else {
            return "ok";
        }
    }

    public String runRetries(int failedAttempts) throws Exception {
        return runRetriesWithTimeout(failedAttempts, 0);
    }

    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @RetryUnwrap
    @Retry(retryOn = {RetryWrapperException.class}) // 3 retries, 200ms jitter
    public String withoutRetry1() {
        counter++;
        var ex = new AccountManagementSystemClientException("Message", 401);
        throw ex.convert();
    }

    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @RetryUnwrap
    @Retry(retryOn = {RetryWrapperException.class}) // 3 retries, 200ms jitter
    public String withoutRetry2() {
        counter++;
        var ex = new AccountManagementSystemClientException(new IllegalStateException());
        throw ex.convert();
    }

    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @RetryUnwrap
    @Retry(retryOn = {RetryWrapperException.class}) // 3 retries, 200ms jitter
    public String withRetry1() {
        counter++;
        var ex = new AccountManagementSystemClientException("Message", 500);
        throw ex.convert();
    }

    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @RetryUnwrap
    @Retry(retryOn = {RetryWrapperException.class}) // 3 retries, 200ms jitter
    public String withRetry2() {
        counter++;
        var ex = new AccountManagementSystemClientException(new IOException());
        throw ex.convert();
    }

    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @RetryUnwrap
    @Retry(retryOn = {RetryWrapperException.class}) // 3 retries, 200ms jitter
    public String timeout(int millis) throws InterruptedException {
        counter++;
        Thread.sleep(millis);
        return "ok";
    }

    public void reset() {
        counter = 0;
    }
}
