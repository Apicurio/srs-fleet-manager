package org.bf2.srs.fleetmanager.execution.impl.workers;

import org.bf2.srs.fleetmanager.spi.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class Utils {

    private Utils() {
    }

    public static TenantManagerConfig createTenantManagerConfig(RegistryDeploymentData registryDeployment) {
        return TenantManagerConfig.builder()
                .tenantManagerUrl(registryDeployment.getTenantManagerUrl())
                .registryDeploymentUrl(registryDeployment.getRegistryDeploymentUrl())
                .build();
    }
}
