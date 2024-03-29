package org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision;

import static java.util.Objects.requireNonNull;
import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.EVAL_INSTANCE_EXPIRATION_REGISTRY_T;

import org.bf2.srs.fleetmanager.execution.impl.tasks.AbstractTask;
import org.bf2.srs.fleetmanager.execution.manager.TaskSchedule;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class EvalInstanceExpirationRegistryTask extends AbstractTask {

    private String registryId;

    @Builder
    public EvalInstanceExpirationRegistryTask(String registryId, TaskSchedule schedule) {
        super(EVAL_INSTANCE_EXPIRATION_REGISTRY_T);
        requireNonNull(registryId);
        requireNonNull(schedule);
        this.registryId = registryId;
        this.schedule = schedule;
    }
}
