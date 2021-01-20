package io.bf2fc6cc711aee1a0c2a.execution.tasks.impl;

import io.bf2fc6cc711aee1a0c2a.execution.tasks.Task;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskSchedule;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public abstract class AbstractTask implements Task {

    @EqualsAndHashCode.Include
    private TaskType taskType;

    @EqualsAndHashCode.Include
    protected String id;

    protected TaskSchedule taskSchedule;

    public String getId() {
        requireNonNull(id);
        return id;
    }

    protected AbstractTask(TaskType taskType) {
        requireNonNull(taskType);
        this.taskType = taskType;

        this.id = UUID.randomUUID().toString();

        taskSchedule = TaskSchedule.builder().build();
    }
}
