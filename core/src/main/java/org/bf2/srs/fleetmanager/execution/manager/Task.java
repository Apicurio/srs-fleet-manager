package org.bf2.srs.fleetmanager.execution.manager;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.bf2.srs.fleetmanager.operation.OperationContextData;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "$class")
public interface Task {

    /**
     * @return a globally unique task identifier (NOT only within a given type).
     */
    String getId();

    /**
     * A way for tasks to be logically grouped, and assigned to workers.
     */
    String getType();

    TaskSchedule getSchedule();

    /**
     * May return null.
     */
    OperationContextData getOperationContextData();

    void setOperationContextData(OperationContextData operationContextData);
}
