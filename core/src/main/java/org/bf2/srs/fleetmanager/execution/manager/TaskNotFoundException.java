package org.bf2.srs.fleetmanager.execution.manager;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class TaskNotFoundException extends Exception {

    private static final long serialVersionUID = 825807145266618478L;

    public TaskNotFoundException() {
        super();
    }

    public TaskNotFoundException(String message) {
        super(message);
    }

    public static TaskNotFoundException create(String id) {
        return new TaskNotFoundException("No Task found for id " + id);
    }
}
