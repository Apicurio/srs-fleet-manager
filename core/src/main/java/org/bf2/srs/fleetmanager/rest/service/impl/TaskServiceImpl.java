package org.bf2.srs.fleetmanager.rest.service.impl;

import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.TaskNotFoundException;
import org.bf2.srs.fleetmanager.rest.service.TaskService;
import org.bf2.srs.fleetmanager.rest.service.convert.ConvertTask;
import org.bf2.srs.fleetmanager.rest.service.model.Task;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class TaskServiceImpl implements TaskService {

    @Inject
    TaskManager taskManager;

    @Inject
    ConvertTask convertTask;

    @Override
    public List<Task> getTasks() {
        return taskManager.getAllTasks().stream()
                .map(convertTask::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Task getTask(String id) throws TaskNotFoundException {
        return taskManager.getTaskById(id)
                .map(convertTask::convert)
                .orElseThrow(() -> TaskNotFoundException.create(id));
    }
}
