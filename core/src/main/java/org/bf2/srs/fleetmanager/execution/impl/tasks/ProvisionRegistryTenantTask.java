package org.bf2.srs.fleetmanager.execution.impl.tasks;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.PROVISION_REGISTRY_TENANT_T;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class ProvisionRegistryTenantTask extends AbstractTask {

    private long registryId;

    @Setter
    private String registryTenantId;

    @Builder
    public ProvisionRegistryTenantTask(long registryId) {
        super(PROVISION_REGISTRY_TENANT_T);
        this.registryId = registryId;
    }
}
