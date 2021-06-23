package org.bf2.srs.fleetmanager.rest.service;

import org.bf2.srs.fleetmanager.execution.manager.TaskNotFoundException;
import org.bf2.srs.fleetmanager.rest.service.model.Task;

import java.util.List;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public interface TaskService {

    List<Task> getTasks();

    Task getTask(String id) throws TaskNotFoundException;
}
