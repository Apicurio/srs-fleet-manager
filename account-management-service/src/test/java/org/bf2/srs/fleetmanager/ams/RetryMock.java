package org.bf2.srs.fleetmanager.ams;

import lombok.Getter;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.FaultToleranceConstants;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.RetryUnwrap;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.RetryWrap;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.RetryWrapperException;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.io.IOException;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RetryMock {

    @Getter
    private int counter = 0;

    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @RetryUnwrap
    @Retry(retryOn = {RetryWrapperException.class}) // 3 retries, 200ms jitter
    @RetryWrap
    public String runRetriesWithTimeout(int failedAttempts, int millis) throws Exception {
        Thread.sleep(millis);
        counter++;
        if (counter <= failedAttempts) {
            throw new AccountManagementServiceException(Optional.empty(), Optional.of(500), new RuntimeException());
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
    @RetryWrap
    public String withoutRetry1() throws AccountManagementServiceException {
        counter++;
        throw new AccountManagementServiceException(Optional.empty(), Optional.of(401), new RuntimeException());
    }

    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @RetryUnwrap
    @Retry(retryOn = {RetryWrapperException.class}) // 3 retries, 200ms jitter
    @RetryWrap
    public String withoutRetry2() throws AccountManagementServiceException {
        counter++;
        throw new AccountManagementServiceException(Optional.empty(), Optional.empty(), new RuntimeException());
    }

    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @RetryUnwrap
    @Retry(retryOn = {RetryWrapperException.class}) // 3 retries, 200ms jitter
    @RetryWrap
    public String withRetry1() throws AccountManagementServiceException {
        counter++;
        throw new AccountManagementServiceException(Optional.empty(), Optional.of(500), new RuntimeException());
    }

    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @RetryUnwrap
    @Retry(retryOn = {RetryWrapperException.class}) // 3 retries, 200ms jitter
    @RetryWrap
    public String withRetry2() throws AccountManagementServiceException {
        counter++;
        throw new AccountManagementServiceException(Optional.empty(), Optional.empty(), new IOException());
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
