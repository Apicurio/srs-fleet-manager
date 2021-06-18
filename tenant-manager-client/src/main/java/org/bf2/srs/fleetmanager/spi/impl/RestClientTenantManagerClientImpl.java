package org.bf2.srs.fleetmanager.spi.impl;

import io.apicurio.multitenant.api.datamodel.NewRegistryTenantRequest;
import io.apicurio.multitenant.api.datamodel.RegistryTenant;
import io.apicurio.multitenant.client.Auth;
import io.apicurio.multitenant.client.TenantManagerClientImpl;
import org.bf2.srs.fleetmanager.spi.TenantManagerClient;
import org.bf2.srs.fleetmanager.spi.model.Tenant;
import org.bf2.srs.fleetmanager.spi.model.TenantManager;
import org.bf2.srs.fleetmanager.spi.model.TenantRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RestClientTenantManagerClientImpl implements TenantManagerClient {

    private final Auth auth;

    private Map<String, TenantManagerClientImpl> pool = new ConcurrentHashMap<String, TenantManagerClientImpl>();

    public RestClientTenantManagerClientImpl() {
        this(null);
    }

    public RestClientTenantManagerClientImpl(Auth auth) {
        this.auth = auth;
    }

    private io.apicurio.multitenant.client.TenantManagerClient getClient(TenantManager tm) {
        return pool.computeIfAbsent(tm.getTenantManagerUrl(), k -> {
            if (auth != null) {
                return new TenantManagerClientImpl(tm.getTenantManagerUrl(), auth);
            } else {
               return new TenantManagerClientImpl(tm.getTenantManagerUrl());
            }
        });
    }

    @Override
    public Tenant createTenant(TenantManager tm, TenantRequest tenantRequest) {
        var client = getClient(tm);

        NewRegistryTenantRequest req = new NewRegistryTenantRequest();
        req.setOrganizationId(tenantRequest.getOrganizationId());

        req.setTenantId(tenantRequest.getTenantId());
        RegistryTenant tenant = client.createTenant(req);

        return Tenant.builder()
                .id(tenant.getTenantId())
                .build();
    }

    @Override
    public List<Tenant> getAllTenants(TenantManager tm) {
        var client = getClient(tm);
        return client.listTenants().stream()
                .map(t -> Tenant.builder()
                        .id(t.getTenantId())
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
