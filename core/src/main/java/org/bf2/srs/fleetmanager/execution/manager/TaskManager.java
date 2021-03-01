package org.bf2.srs.fleetmanager.execution.manager;

import java.util.Optional;
import java.util.Set;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public interface TaskManager {

    /**
     * Start the task manager.
     */
    void start();

    /**
     * Submit a new task for execution.
     */
    void submit(Task task);

    /**
     * Get a set of all tasks.
     */
    Set<Task> getAllTasks();

    /**
     * Get a set of all tasks with a given type.
     */
    Set<Task> getTasksByType(String taskType);

    /**
     * Get a task with the given ID, if exists.
     */
    Optional<Task> getTaskById(String taskId);

    /**
     * Remove the task from further execution.
     */
    void remove(Task task);

    /**
     * Stop the task manager.
     */
    void stop();
}
