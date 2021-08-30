package org.bf2.srs.fleetmanager.execution.impl.tasks;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.SCHEDULE_REGISTRY_T;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class ScheduleRegistryTask extends AbstractTask {

    private String registryId;

    @Builder
    public ScheduleRegistryTask(String registryId) {
        super(SCHEDULE_REGISTRY_T);
        this.registryId = registryId;
    }
}
