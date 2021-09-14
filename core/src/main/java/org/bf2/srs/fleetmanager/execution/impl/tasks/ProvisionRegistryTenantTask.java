package org.bf2.srs.fleetmanager.execution.impl.tasks;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static java.util.Objects.requireNonNull;
import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.PROVISION_REGISTRY_TENANT_T;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class ProvisionRegistryTenantTask extends AbstractTask {

    private String registryId;

    @Setter
    private String registryTenantId;

    @Builder
    public ProvisionRegistryTenantTask(String registryId) {
        super(PROVISION_REGISTRY_TENANT_T);
        requireNonNull(registryId);
        this.registryId = registryId;
    }
}
