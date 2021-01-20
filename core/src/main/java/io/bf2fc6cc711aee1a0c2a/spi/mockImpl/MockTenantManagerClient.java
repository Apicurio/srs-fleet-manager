package io.bf2fc6cc711aee1a0c2a.spi.mockImpl;

import io.bf2fc6cc711aee1a0c2a.spi.TenantManagerClient;
import io.bf2fc6cc711aee1a0c2a.spi.model.Tenant;
import io.bf2fc6cc711aee1a0c2a.spi.model.TenantManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MockTenantManagerClient implements TenantManagerClient {

    private final Map<TenantManager, Set<Tenant>> testData = new HashMap<>();

    private void init(TenantManager tm) {
        testData.computeIfAbsent(tm, s -> new HashSet<>());
    }

    @Override
    public Tenant createTenant(TenantManager tm) {
        String tenantID = "tenant-" + UUID.randomUUID();
        Tenant tenant = Tenant.builder().id(tenantID).tenantApiUrl("https://registry.app.example.com/" + tenantID).build();
        init(tm);
        testData.get(tm).add(tenant);
        return tenant;
    }

    @Override
    public List<Tenant> getAllTenants(TenantManager tm) {
        init(tm);
        return new ArrayList<>(testData.get(tm));
    }

    @Override
    public void deleteTenant(TenantManager tm, Tenant tenant) {
        init(tm);
        testData.get(tm).remove(tenant);
    }

    @Override
    public boolean pingTenantManager(TenantManager tm) {
        return true;
    }

    @Override
    public boolean pingTenant(TenantManager tm, Tenant tenant) {
        init(tm);
        return testData.get(tm).contains(tenant);
    }
}
