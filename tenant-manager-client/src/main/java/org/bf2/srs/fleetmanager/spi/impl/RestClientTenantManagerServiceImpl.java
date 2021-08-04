package org.bf2.srs.fleetmanager.spi.impl;

import io.apicurio.multitenant.api.datamodel.NewRegistryTenantRequest;
import io.apicurio.multitenant.api.datamodel.RegistryTenant;
import io.apicurio.multitenant.api.datamodel.ResourceType;
import io.apicurio.multitenant.api.datamodel.TenantResource;
import io.apicurio.multitenant.client.TenantManagerClient;
import io.apicurio.multitenant.client.TenantManagerClientImpl;
import io.apicurio.rest.client.auth.Auth;
import org.bf2.srs.fleetmanager.spi.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.model.CreateTenantRequest;
import org.bf2.srs.fleetmanager.spi.model.Tenant;
import org.bf2.srs.fleetmanager.spi.model.TenantLimit;
import org.bf2.srs.fleetmanager.spi.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.spi.model.TenantStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RestClientTenantManagerServiceImpl implements TenantManagerService {

    private final Auth auth;

    // TODO Data is never deleted! Prevent OOM error.
    private Map<String, TenantManagerClientImpl> pool = new ConcurrentHashMap<String, TenantManagerClientImpl>();

    public RestClientTenantManagerServiceImpl() {
        this(null);
    }

    public RestClientTenantManagerServiceImpl(Auth auth) {
        this.auth = auth;
    }

    private TenantManagerClient getClient(TenantManagerConfig tm) {
        return pool.computeIfAbsent(tm.getTenantManagerUrl(), k -> {
            if (auth != null) {
                return new TenantManagerClientImpl(tm.getTenantManagerUrl(), Collections.emptyMap(), auth);
            } else {
                return new TenantManagerClientImpl(tm.getTenantManagerUrl());
            }
        });
    }

    private Tenant convert(RegistryTenant data) {
        return Tenant.builder()
                .id(data.getTenantId())
                .status(TenantStatus.fromValue(data.getStatus().value()))
                .build();
    }

    @Override
    public Tenant createTenant(TenantManagerConfig tm, CreateTenantRequest tenantRequest) {
        var client = getClient(tm);

        NewRegistryTenantRequest req = new NewRegistryTenantRequest();
        req.setOrganizationId(tenantRequest.getOrganizationId());
        req.setTenantId(tenantRequest.getTenantId());
        req.setCreatedBy(tenantRequest.getCreatedBy());

        req.setResources(Optional.ofNullable(tenantRequest.getResources()).stream()
                .flatMap(Collection::stream)
                .map(r -> {
                    TenantResource tr = new TenantResource();
                    tr.setType(ResourceType.fromValue(r.getType()));
                    tr.setLimit(r.getLimit());
                    return tr;
                })
                .collect(Collectors.toList()));

        RegistryTenant tenant = client.createTenant(req);

        return convert(tenant);
    }

    @Override
    public Optional<Tenant> getTenantById(TenantManagerConfig tm, String tenantId) {
        var client = getClient(tm);
        try {
            RegistryTenant tenant = client.getTenant(tenantId);
            return Optional.of(convert(tenant));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Tenant> getAllTenants(TenantManagerConfig tm) {
        var client = getClient(tm);
        return client.listTenants().stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTenant(TenantManagerConfig tm, String tenantId) {
        var client = getClient(tm);
        client.deleteTenant(tenantId);
    }

    @Override
    public boolean pingTenantManager(TenantManagerConfig tm) {
        // TODO implement
        return true;
    }

    @Override
    public boolean pingTenant(TenantManagerConfig tm, String tenantId) {
        // TODO implement
        return true;
    }

    /**
     * @see org.bf2.srs.fleetmanager.spi.TenantManagerService#validateConfig(java.util.List)
     */
    @Override
    public void validateConfig(List<TenantLimit> limits) {
        if (limits == null) {
            return;
        }
        for (var limit : limits) {
            //this will throw an exception if any limit type is not valid
            ResourceType.fromValue(limit.getType());
        }
    }

}
