package org.bf2.srs.fleetmanager.spi;

import org.bf2.srs.fleetmanager.spi.model.Tenant;
import org.bf2.srs.fleetmanager.spi.model.TenantLimit;
import org.bf2.srs.fleetmanager.spi.model.TenantManager;
import org.bf2.srs.fleetmanager.spi.model.TenantRequest;

import java.util.List;

public interface TenantManagerClient {

    Tenant createTenant(TenantManager tm, TenantRequest tenantRequest);

    List<Tenant> getAllTenants(TenantManager tm);

    void deleteTenant(TenantManager tm, String tenantId);

    /////

    boolean pingTenantManager(TenantManager tm);

    boolean pingTenant(TenantManager tm, String tenantId);

    default void validateConfig(List<TenantLimit> limits) {}
}
