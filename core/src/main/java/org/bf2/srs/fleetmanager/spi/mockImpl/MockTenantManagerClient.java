package org.bf2.srs.fleetmanager.spi.mockImpl;

import org.bf2.srs.fleetmanager.spi.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.model.CreateTenantRequest;
import org.bf2.srs.fleetmanager.spi.model.Tenant;
import org.bf2.srs.fleetmanager.spi.model.TenantLimit;
import org.bf2.srs.fleetmanager.spi.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.spi.model.TenantStatus;
import org.bf2.srs.fleetmanager.spi.model.UpdateTenantRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public class MockTenantManagerClient implements TenantManagerService {

    private final Map<TenantManagerConfig, Map<String, Tenant>> testData = new ConcurrentHashMap<>();

    private void init(TenantManagerConfig tm) {
        testData.computeIfAbsent(tm, s -> new ConcurrentHashMap<>());
    }

    @Override
    public Tenant createTenant(TenantManagerConfig tm, CreateTenantRequest req) {
        requireNonNull(tm);
        requireNonNull(req);

        Tenant tenant = Tenant.builder()
                .id(req.getTenantId())
                .build();
        init(tm);
        testData.get(tm).put(tenant.getId(), tenant);
        return tenant;
    }

    @Override
    public Optional<Tenant> getTenantById(TenantManagerConfig tm, String tenantId) {
        init(tm);
        return Optional.ofNullable(testData.get(tm).get(tenantId));
    }

    @Override
    public List<Tenant> getAllTenants(TenantManagerConfig tm) {
        init(tm);
        return new ArrayList<>(testData.get(tm).values());
    }

    @Override
    public void updateTenant(TenantManagerConfig tm, UpdateTenantRequest req) {
        requireNonNull(tm);
        requireNonNull(req);
        init(tm);
        var optionalTenant = getTenantById(tm, req.getId());
        var tenant = optionalTenant.orElseThrow(() -> new IllegalArgumentException("No tenant found for ID " + req.getId())); // TODO
        tenant.setStatus(req.getStatus());
        testData.get(tm).put(req.getId(), tenant);
    }

    @Override
    public void deleteTenant(TenantManagerConfig tm, String tenantId) {
        requireNonNull(tm);
        requireNonNull(tenantId);
        init(tm);
        testData.get(tm).get(tenantId).setStatus(TenantStatus.TO_BE_DELETED);
    }

    @Override
    public boolean pingTenantManager(TenantManagerConfig tm) {
        requireNonNull(tm);
        return true;
    }

    @Override
    public boolean pingTenant(TenantManagerConfig tm, String tenantId) {
        requireNonNull(tm);
        requireNonNull(tenantId);
        init(tm);
        return testData.get(tm).containsKey(tenantId);
    }

    @Override
    public void validateConfig(List<TenantLimit> limits) {
        // NOOP
    }
}
