package io.bf2fc6cc711aee1a0c2a.execution.manager;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class TaskSchedule {

    public static int PRIORITY_LOW = 0;
    public static int PRIORITY_DEFAULT = 5;
    public static int PRIORITY_HIGH = 10;

    public static final int MIN_RETRIES_DEFAULT = 10; // 0,1,2,4,8,16,32,64,128,256 seconds

    private Instant firstExecuteAt;

    // TODO Optional?
    private Duration interval;

    private int minRetries;

    private int priority;

    @Builder
    private TaskSchedule(Instant firstExecuteAt, Duration interval, Integer minRetries, Integer priority) {
        if (firstExecuteAt == null)
            firstExecuteAt = Instant.now();
        this.firstExecuteAt = firstExecuteAt;

        this.interval = interval; // Can be null
        if (interval != null && interval.isNegative()) {
            throw new IllegalArgumentException("Negative interval is not allowed.");
        }

        if (minRetries == null)
            minRetries = MIN_RETRIES_DEFAULT;
        if (minRetries < 0)
            throw new IllegalArgumentException("Negative minRetries is not allowed.");
        this.minRetries = minRetries;

        if (priority == null)
            priority = PRIORITY_DEFAULT;
        this.priority = priority;
    }
}
