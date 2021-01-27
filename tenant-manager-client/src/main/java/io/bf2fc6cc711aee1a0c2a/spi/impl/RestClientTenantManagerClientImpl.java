package io.bf2fc6cc711aee1a0c2a.spi.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.apicurio.multitenant.client.TenantManagerClientImpl;
import io.apicurio.multitenant.datamodel.NewRegistryTenantRequest;
import io.apicurio.multitenant.datamodel.RegistryTenant;
import io.bf2fc6cc711aee1a0c2a.spi.TenantManagerClient;
import io.bf2fc6cc711aee1a0c2a.spi.model.Tenant;
import io.bf2fc6cc711aee1a0c2a.spi.model.TenantManager;
import io.bf2fc6cc711aee1a0c2a.spi.model.TenantRequest;

public class RestClientTenantManagerClientImpl implements TenantManagerClient {

    private Map<String, TenantManagerClientImpl> pool = new ConcurrentHashMap<String, TenantManagerClientImpl>();

    private io.apicurio.multitenant.client.TenantManagerClient getClient(TenantManager tm) {
        return pool.computeIfAbsent(tm.getTenantManagerUrl(), k -> {
            return new TenantManagerClientImpl(tm.getTenantManagerUrl());
        });
    }

    @Override
    public Tenant createTenant(TenantManager tm, TenantRequest tenantRequest) {
        var client = getClient(tm);

        NewRegistryTenantRequest req = new NewRegistryTenantRequest();
        req.setDeploymentFlavor("small"); //TODO
        req.setOrganizationId("unknown"); //TODO pick from authentication details?

        req.setAuthServerUrl(tenantRequest.getAuthServerUrl());
        req.setClientId(tenantRequest.getAuthClientId());

        RegistryTenant tenant = client.createTenant(req);

        return Tenant.builder()
                .id(tenant.getTenantId())

                //TODO because we are not using a proxy yet, we just return the deployment url, in the future tenant-manager will return the configured url in the proxy
                .tenantApiUrl(tm.getRegistryDeploymentUrl())

                .build();
    }

    @Override
    public List<Tenant> getAllTenants(TenantManager tm) {
        var client = getClient(tm);
        return client.listTenants().stream()
                    .map(t -> Tenant.builder()
                                .id(t.getTenantId())
                                .authServerUrl(t.getAuthServerUrl())
                                .authClientId(t.getAuthClientId())

                                //TODO tenant manager will provide this info
                                .tenantApiUrl(tm.getRegistryDeploymentUrl())

                                .build())
                    .collect(Collectors.toList());
    }

    @Override
    public void deleteTenant(TenantManager tm, String tenantId) {
        var client = getClient(tm);
        client.deleteTenant(tenantId);
    }

    @Override
    public boolean pingTenantManager(TenantManager tm) {
        // TODO implement
        return true;
    }

    @Override
    public boolean pingTenant(TenantManager tm, String tenantId) {
        // TODO implement
        return true;
    }

}
