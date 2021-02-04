package io.bf2fc6cc711aee1a0c2a.execution.impl.tasks;

import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskSchedule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;

import static io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.TaskType.REGISTRY_DEPLOYMENT_HEARTBEAT_T;

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
