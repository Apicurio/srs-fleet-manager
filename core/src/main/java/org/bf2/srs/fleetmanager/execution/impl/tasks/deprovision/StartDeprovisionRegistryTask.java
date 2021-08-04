package org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bf2.srs.fleetmanager.execution.impl.tasks.AbstractTask;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.START_DEPROVISION_REGISTRY_T;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class StartDeprovisionRegistryTask extends AbstractTask {

    private long registryId;

    @Setter
    private String registryTenantId;

    @Builder
    public StartDeprovisionRegistryTask(long registryId) {
        super(START_DEPROVISION_REGISTRY_T);
        this.registryId = registryId;
    }
}
