package io.bf2fc6cc711aee1a0c2a.execution.tasks;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class TaskSchedule {

    public static int LOW_PRIORITY = 0;
    public static int DEFAULT_PRIORITY = 5;
    public static int HIGH_PRIORITY = 10;

    private Instant firstExecuteAt;

    private Duration interval; // TODO Optional

    private boolean persistent;

    private int priority;

    @Builder
    private TaskSchedule(Instant firstExecuteAt, Duration interval, Boolean persistent, Integer priority) {
        if (firstExecuteAt == null)
            firstExecuteAt = Instant.now();
        this.firstExecuteAt = firstExecuteAt;

        this.interval = interval; // Can be null

        if (persistent == null)
            this.persistent = false;

        if (priority == null)
            priority = DEFAULT_PRIORITY;
        this.priority = priority;
    }
}
