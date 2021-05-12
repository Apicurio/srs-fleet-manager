package org.bf2.srs.fleetmanager.spi.mockImpl;

import org.bf2.srs.fleetmanager.spi.TenantManagerClient;
import org.bf2.srs.fleetmanager.spi.model.Tenant;
import org.bf2.srs.fleetmanager.spi.model.TenantManager;
import org.bf2.srs.fleetmanager.spi.model.TenantRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public class MockTenantManagerClient implements TenantManagerClient {

    private final Map<TenantManager, Map<String, Tenant>> testData = new ConcurrentHashMap<>();

    private void init(TenantManager tm) {
        testData.computeIfAbsent(tm, s -> new ConcurrentHashMap<>());
    }

    @Override
    public Tenant createTenant(TenantManager tm, TenantRequest req) {
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
    public List<Tenant> getAllTenants(TenantManager tm) {
        init(tm);
        return new ArrayList<>(testData.get(tm).values());
    }

    @Override
    public void deleteTenant(TenantManager tm, String tenantId) {
        requireNonNull(tm);
        requireNonNull(tenantId);
        init(tm);
        testData.get(tm).remove(tenantId);
    }

    @Override
    public boolean pingTenantManager(TenantManager tm) {
        requireNonNull(tm);
        return true;
    }

    @Override
    public boolean pingTenant(TenantManager tm, String tenantId) {
        requireNonNull(tm);
        requireNonNull(tenantId);
        init(tm);
        return testData.get(tm).containsKey(tenantId);
    }
}
