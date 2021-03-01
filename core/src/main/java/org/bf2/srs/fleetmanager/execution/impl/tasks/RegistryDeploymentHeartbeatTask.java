package org.bf2.srs.fleetmanager.execution.impl.tasks;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bf2.srs.fleetmanager.execution.manager.TaskSchedule;

import java.time.Duration;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.REGISTRY_DEPLOYMENT_HEARTBEAT_T;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class RegistryDeploymentHeartbeatTask extends AbstractTask {

    private long deploymentId;

    @Builder
    public RegistryDeploymentHeartbeatTask(long deploymentId) {
        super(REGISTRY_DEPLOYMENT_HEARTBEAT_T);
        this.deploymentId = deploymentId;
        this.schedule = TaskSchedule.builder().interval(Duration.ofSeconds(10)).build();
    }
}
