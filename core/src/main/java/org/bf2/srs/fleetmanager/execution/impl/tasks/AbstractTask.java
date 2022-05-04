package org.bf2.srs.fleetmanager.execution.impl.tasks;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskSchedule;
import org.bf2.srs.fleetmanager.operation.OperationContextData;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * WARNING: This class and its contents MUST be serializable (and deserializable) to JSON using ObjectMapper.
 * When performing modifications, make sure previous values remain deserializable or are otherwise handled.
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public abstract class AbstractTask implements Task {

    @EqualsAndHashCode.Include
    protected String id;

    protected String type;

    @Setter
    protected TaskSchedule schedule;

    @Setter
    protected OperationContextData operationContextData;

    protected AbstractTask(TaskType type) {
        requireNonNull(type);
        this.id = UUID.randomUUID().toString();
        this.type = type.name();
        this.schedule = TaskSchedule.builder().build();
    }
}
