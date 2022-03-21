package org.bf2.srs.fleetmanager.ams;

import io.quarkus.test.junit.QuarkusTest;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class RetryTest {

    @Inject
    RetryMock mock;

    @BeforeEach
    void beforeEach() {
        mock.reset();
    }

    @Test
    void testRetries() throws Exception {
        var res = mock.runRetries(1);
        Assertions.assertEquals("ok", res);
        Assertions.assertEquals(2, mock.getCounter());
        mock.reset();

        res = mock.runRetries(3);
        Assertions.assertEquals("ok", res);
        Assertions.assertEquals(4, mock.getCounter());
        mock.reset();

        try {
            mock.runRetries(4);
            Assertions.fail("Should throw an exception");
        } catch (AccountManagementServiceException ex) {
            // ok
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception" + ex);
        }
    }

    @Test
    void testFailWithRetry() {
        try {
            mock.withRetry1();
            Assertions.fail("Should throw an exception");
        } catch (AccountManagementServiceException ex) {
            Assertions.assertEquals(4, mock.getCounter());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception" + ex);
        }
        mock.reset();
        try {
            mock.withRetry2();
            Assertions.fail("Should throw an exception");
        } catch (AccountManagementServiceException ex) {
            Assertions.assertEquals(4, mock.getCounter());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception" + ex);
        }
    }

    @Test
    void testFailWithoutRetry() {
        try {
            mock.withoutRetry1();
            Assertions.fail("Should throw an exception");
        } catch (AccountManagementServiceException ex) {
            Assertions.assertEquals(1, mock.getCounter());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception" + ex);
        }
        mock.reset();
        try {
            mock.withoutRetry2();
            Assertions.fail("Should throw an exception");
        } catch (AccountManagementServiceException ex) {
            Assertions.assertEquals(1, mock.getCounter());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception" + ex);
        }
    }

    @Test
    void testNoTimeout() {
        try {
            var res = mock.timeout(2000);
            Assertions.assertEquals("ok", res);
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception" + ex);
        }
    }

    @Test
    void testTimeout() {
        try {
            mock.timeout(3500);
            Assertions.fail("Should throw an exception");
        } catch (TimeoutException | InterruptedException ex) {
            Assertions.assertEquals(1, mock.getCounter());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception" + ex);
        }
    }

    @Test
    void testRetriesWithTimeout() throws Exception {
        // Timeout is applied separately for each retry!
        // If a retry fails with a timeout, it is not retried again.
        var res = mock.runRetriesWithTimeout(1, 2500);
        Assertions.assertEquals("ok", res);
        Assertions.assertEquals(2, mock.getCounter());
        mock.reset();

        try {
            mock.runRetriesWithTimeout(1, 3500);
            Assertions.fail("Should throw an exception");
        } catch (TimeoutException|InterruptedException ex) {
            Assertions.assertEquals(0, mock.getCounter());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception" + ex);
        }
    }
}
