package io.bf2fc6cc711aee1a0c2a.execution.manager;

import io.bf2fc6cc711aee1a0c2a.execution.tasks.Task;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType;

import java.util.Optional;
import java.util.Set;

public interface TaskManager {

    void start();

    void submit(Task task);

    Set<Task> getAllTasks();

    Set<Task> getTasksByType(TaskType taskType);

    Optional<Task> getTaskById(String taskId);

    void remove(Task task);

    void stop();
}
