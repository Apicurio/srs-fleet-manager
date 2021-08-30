package org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bf2.srs.fleetmanager.execution.impl.tasks.AbstractTask;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.DEPROVISION_REGISTRY_T;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class DeprovisionRegistryTask extends AbstractTask {

    private String registryId;

    @Setter
    private String registryTenantId;

    @Setter
    private boolean amsSuccess = false;

    @Builder
    public DeprovisionRegistryTask(String registryId) {
        super(DEPROVISION_REGISTRY_T);
        this.registryId = registryId;
    }
}
