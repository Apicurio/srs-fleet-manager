package io.bf2fc6cc711aee1a0c2a.execution.jobs;

import io.bf2fc6cc711aee1a0c2a.execution.tasks.Task;

public interface Worker {

    boolean supports(Task taskType);

    void execute(Task task);
}
