package org.bf2.srs.fleetmanager.execution.impl.workers;

import org.bf2.srs.fleetmanager.execution.manager.Worker;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.util.Objects.requireNonNull;

/**
 * Workers MUST be thread safe. They should not contain state and inject thread safe beans only.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@NoArgsConstructor
@Getter
@ToString
public abstract class AbstractWorker implements Worker {

    protected String type;

    protected AbstractWorker(WorkerType type) {
        requireNonNull(type);
        this.type = type.name();
    }
}
