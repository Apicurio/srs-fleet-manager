package org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bf2.srs.fleetmanager.execution.impl.tasks.AbstractTask;
import org.bf2.srs.fleetmanager.execution.manager.TaskSchedule;

import java.time.Duration;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.CHECK_REGISTRY_DELETED_T;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class CheckRegistryDeletedTask extends AbstractTask {

    private long registryId;

    @Setter
    private boolean amsSuccess = false;

    @Setter
    private String registryTenantId;

    @Builder
    public CheckRegistryDeletedTask(long registryId) {
        super(CHECK_REGISTRY_DELETED_T);
        this.registryId = registryId;
        this.schedule = TaskSchedule.builder().interval(Duration.ofSeconds(60)).build();
    }
}
