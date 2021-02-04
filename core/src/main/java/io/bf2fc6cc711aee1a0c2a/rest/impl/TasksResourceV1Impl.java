package io.bf2fc6cc711aee1a0c2a.rest.impl;

import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskNotFoundException;
import io.bf2fc6cc711aee1a0c2a.rest.TasksResourceV1;
import io.bf2fc6cc711aee1a0c2a.rest.convert.ConvertTask;
import io.bf2fc6cc711aee1a0c2a.rest.model.TaskRest;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class TasksResourceV1Impl implements TasksResourceV1 {

    @Inject
    TaskManager taskManager;

    @Inject
    ConvertTask convertTask;

    @Override
    public List<TaskRest> getTasks() {
        return taskManager.getAllTasks().stream()
                .map(convertTask::convert)
                .collect(Collectors.toList());
    }

    @Override
    public TaskRest getTask(String id) throws TaskNotFoundException {
        return taskManager.getTaskById(id)
                .map(convertTask::convert)
                .orElseThrow(() -> TaskNotFoundException.create(id));
    }
}
