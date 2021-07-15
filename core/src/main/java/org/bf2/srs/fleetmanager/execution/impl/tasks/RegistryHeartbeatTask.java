package org.bf2.srs.fleetmanager.execution.impl.tasks;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bf2.srs.fleetmanager.execution.manager.TaskSchedule;

import java.time.Duration;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.REGISTRY_HEARTBEAT_T;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class RegistryHeartbeatTask extends AbstractTask {

    private long registryId;

    @Builder
    public RegistryHeartbeatTask(long registryId) {
        super(REGISTRY_HEARTBEAT_T);
        this.registryId = registryId;
        this.schedule = TaskSchedule.builder().interval(Duration.ofSeconds(30)).build();
    }
}
