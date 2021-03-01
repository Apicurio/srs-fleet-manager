package org.bf2.srs.fleetmanager.rest.impl;

import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.TaskNotFoundException;
import org.bf2.srs.fleetmanager.rest.TasksResourceV1;
import org.bf2.srs.fleetmanager.rest.convert.ConvertTask;
import org.bf2.srs.fleetmanager.rest.model.TaskRest;

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
