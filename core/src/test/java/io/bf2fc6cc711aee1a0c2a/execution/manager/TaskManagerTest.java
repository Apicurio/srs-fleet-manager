package io.bf2fc6cc711aee1a0c2a.execution.manager;

import io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.TestTask;
import io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.TestTask.BasicCommand;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;

import static io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.TestTask.RetryCommand;
import static java.lang.Long.valueOf;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static java.time.Instant.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@QuarkusTest
public class TaskManagerTest {

    @Inject
    TaskManager tasks;

    @Inject
    DataCollector data;

    @Test
    void testInputs() {
        TestTask task = null;

        // Zero period is OK, but not recommended
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).interval(Duration.ZERO).build()).build()
                .andThen(BasicCommand.builder().increment(true).build())
                .andThen(BasicCommand.builder().increment(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(2));
        assertThat(data.getCounter(), equalTo(2));
        data.reset();

        try {
            TaskSchedule.builder().minRetries(-1).build();
            fail("Illegal argument.");
        } catch (IllegalArgumentException ex) {
            // OK
        }

        try {
            TaskSchedule.builder().interval(Duration.ofSeconds(-1)).build();
            fail("Illegal argument.");
        } catch (IllegalArgumentException ex) {
            // OK
        }
    }

    @Test
    void testAutomaticRetry() {
        TestTask task = null;

        // Test simple automatic retry, with default minRetries
        // Test that there are no repeats after success
        task = TestTask.builder().schedule(TaskSchedule.builder().build()).build()
                .andThen(BasicCommand.builder().throwNPE(true).build())
                .andThen(BasicCommand.builder().increment(true).build())
                .andThen(BasicCommand.builder().increment(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(2));
        assertThat(data.getCounter(), equalTo(1));
        data.reset();

        // Test simple automatic retry with too little minRetries
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).build()).build()
                .andThen(BasicCommand.builder().throwNPE(true).build())
                .andThen(BasicCommand.builder().throwNPE(true).build())
                .andThen(BasicCommand.builder().increment(true).build())
                .andThen(BasicCommand.builder().increment(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(2));
        assertThat(data.getCounter(), equalTo(0));
        data.reset();

        // Test automatic retry with periodic tasks
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).interval(ofSeconds(2)).build()).build()
                .andThen(BasicCommand.builder().increment(true).build())
                .andThen(BasicCommand.builder().throwNPE(true).build())
                .andThen(BasicCommand.builder().increment(true).build())
                .andThen(BasicCommand.builder().increment(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(4));
        assertThat(data.getCounter(), equalTo(3));
        data.reset();

        // Test automatic retry with periodic tasks
        // Even if the interval is very small
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(3).interval(ofMillis(200)).build()).build()
                .andThen(BasicCommand.builder().increment(true).build())
                .andThen(BasicCommand.builder().throwNPE(true).build())
                .andThen(BasicCommand.builder().throwNPE(true).build())
                .andThen(BasicCommand.builder().throwNPE(true).build())
                .andThen(BasicCommand.builder().increment(true).build())
                .andThen(BasicCommand.builder().increment(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(6));
        assertThat(data.getCounter(), equalTo(3));
        data.reset();

        // Even for periodic tasks, passing retry limit stops it
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).interval(ofMillis(200)).build()).build()
                .andThen(BasicCommand.builder().increment(true).build())
                .andThen(BasicCommand.builder().increment(true).build())
                .andThen(BasicCommand.builder().throwNPE(true).build())
                .andThen(BasicCommand.builder().throwNPE(true).build())
                .andThen(BasicCommand.builder().increment(true).build())
                .andThen(BasicCommand.builder().increment(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(4));
        assertThat(data.getCounter(), equalTo(2));
        data.reset();
    }

    @Test
    @Tag("slow")
    void testAutomaticRetrySlow() {
        TestTask task = null;

        // Test automatic retries with default limit (+ backoff timing)
        task = TestTask.builder().schedule(TaskSchedule.builder().build()).build();
        for (int i = 0; i < TaskSchedule.MIN_RETRIES_DEFAULT; i++)
            task.andThen(BasicCommand.builder().throwNPE(true).build());
        task.andThen(BasicCommand.builder().increment(true).build())
                .andThen(BasicCommand.builder().increment(true).build());

        Instant start = now();
        tasks.submit(task);
        data.awaitFinished();
        Duration duration = Duration.between(start, now());

        assertThat(data.getExecutions(), equalTo(TaskSchedule.MIN_RETRIES_DEFAULT + 1));
        assertThat(data.getCounter(), equalTo(1));
        // Assume that the duration grows more than linearly (2sec * N)
        assertThat(duration.getSeconds(), greaterThan(valueOf(TaskSchedule.MIN_RETRIES_DEFAULT * 2)));
        data.reset();
    }

    @Test
    void testManualRetry() {
        TestTask task = null;

        // Test simple manual retry
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).build()).build()
                .andThen(RetryCommand.builder().build())
                .andThen(BasicCommand.builder().increment(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(2));
        assertThat(data.getCounter(), equalTo(1));
        data.reset();

        // Test simple manual retry, over limit
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).build()).build()
                .andThen(RetryCommand.builder().build())
                .andThen(RetryCommand.builder().build())
                .andThen(BasicCommand.builder().increment(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(2));
        assertThat(data.getCounter(), equalTo(0));
        data.reset();

        // Test manual retry, with schedule
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).interval(ofSeconds(2)).build()).build()
                .andThen(BasicCommand.builder().increment(true).build())
                .andThen(BasicCommand.builder().increment(true).build())
                .andThen(RetryCommand.builder().build())
                .andThen(BasicCommand.builder().increment(true).build())
                .andThen(RetryCommand.builder().build())
                .andThen(RetryCommand.builder().build())
                .andThen(BasicCommand.builder().increment(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(6));
        assertThat(data.getCounter(), equalTo(3));
        data.reset();
    }

    @Test
    void testLimitRetry() {
        TestTask task = null;

        // Test simple retry
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).build()).build()
                .andThen(RetryCommand.builder().build())
                .andThen(RetryCommand.builder().minRetries(2).build()) // Request at least two retries (1st)
                .andThen(BasicCommand.builder().increment(true).build()) // OK (2nd)
                .andThen(BasicCommand.builder().increment(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(3));
        assertThat(data.getCounter(), equalTo(1));
        data.reset();

        // Test simple retry, over limit
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).build()).build()
                .andThen(RetryCommand.builder().build())
                .andThen(RetryCommand.builder().minRetries(2).build()) // Request at least two retries (1st)
                .andThen(RetryCommand.builder().build()) // But here is the second
                .andThen(BasicCommand.builder().increment(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(3));
        assertThat(data.getCounter(), equalTo(0));
        data.reset();
    }

    @Test
    void testForceRetry() {
        TestTask task = null;

        // Test simple force retry
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).build()).build()
                .andThen(RetryCommand.builder().build())
                .andThen(RetryCommand.builder().force(true).build()) // Request one more over limit (1st)
                .andThen(BasicCommand.builder().increment(true).build()) // OK (2nd)
                .andThen(BasicCommand.builder().increment(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(3));
        assertThat(data.getCounter(), equalTo(1));
        data.reset();

        // Test force retry, over limit
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).build()).build()
                .andThen(RetryCommand.builder().build())
                .andThen(RetryCommand.builder().minRetries(2).build()) // Request at least two retries (1st)
                .andThen(RetryCommand.builder().force(true).build()) // This should fail, but is force
                .andThen(RetryCommand.builder().force(true).build()) // Force again
                .andThen(BasicCommand.builder().increment(true).build()); // Works

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(5));
        assertThat(data.getCounter(), equalTo(1));
        data.reset();
    }

    @Test
    void testDelay() {
        TestTask task = null;

        // Throw an exception after delaying an increment
        // It will fails since delay is executed only on success
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).build()).build()
                .andThen(RetryCommand.builder().build())
                .andThen(BasicCommand.builder().throwNPE(true).delayIncrement(true).build()) // Throw but delay
                .andThen(BasicCommand.builder().increment(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(2));
        assertThat(data.getCounter(), equalTo(0));
        data.reset();

        // Increment normally + delayed
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).build()).build()
                .andThen(RetryCommand.builder().build())
                .andThen(BasicCommand.builder().increment(true).delayIncrement(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(2));
        assertThat(data.getCounter(), equalTo(2));
        data.reset();
    }

    @Test
    void testFinallyExecute() {
        TestTask task = null;

        // Normally fail
        // Finally is always executed
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).build()).build()
                .andThen(RetryCommand.builder().build())
                .andThen(BasicCommand.builder().throwNPE(true).build());

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(2));
        assertThat(data.getCounter(), equalTo(0));
        assertThat(data.getFinallyExecuteAttempts(), equalTo(1));
        data.reset();

        // Throw an exception finally execute an increment
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).build()).build()
                .andThen(RetryCommand.builder().build())
                .andThen(BasicCommand.builder().throwNPE(true).build()) // Throw
                .finallyExecute(BasicCommand.builder().increment(true).build()); // But increment in finally

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(2));
        assertThat(data.getCounter(), equalTo(1));
        assertThat(data.getFinallyExecuteAttempts(), equalTo(1));
        assertThat(data.getFinallyExecuteSuccess(), equalTo(1));
        data.reset();

        // Throw in finally
        task = TestTask.builder().schedule(TaskSchedule.builder().minRetries(1).build()).build()
                .andThen(RetryCommand.builder().build())
                .andThen(BasicCommand.builder().throwNPE(true).build()) // Throw
                .finallyExecute(BasicCommand.builder().throwNPE(true).build()); // Throw again

        tasks.submit(task);
        data.awaitFinished();
        assertThat(data.getExecutions(), equalTo(2));
        assertThat(data.getCounter(), equalTo(0));
        assertThat(data.getFinallyExecuteAttempts(), equalTo(1));
        assertThat(data.getFinallyExecuteSuccess(), equalTo(0));
        data.reset();
    }
}
