package org.bf2.srs.fleetmanager.spi;

import org.bf2.srs.fleetmanager.spi.model.Tenant;
import org.bf2.srs.fleetmanager.spi.model.TenantLimit;
import org.bf2.srs.fleetmanager.spi.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.spi.model.CreateTenantRequest;
import org.bf2.srs.fleetmanager.spi.model.UpdateTenantRequest;

import java.util.List;
import java.util.Optional;

public interface TenantManagerService {

    Tenant createTenant(TenantManagerConfig tm, CreateTenantRequest tenantRequest);

    //void updateTenant(TenantManagerConfig tm, String tenantId, UpdateTenantRequest request);

    Optional<Tenant> getTenantById(TenantManagerConfig tm, String tenantId);

    /**
     * This operation is costly. Do not use unless required.
     */
    List<Tenant> getAllTenants(TenantManagerConfig tm);

    void updateTenant(TenantManagerConfig tm, UpdateTenantRequest req);

    /**
     * This operation sets tenant status to TO_BE_DELETED.
     */
    void deleteTenant(TenantManagerConfig tm, String tenantId);

    /////

    boolean pingTenantManager(TenantManagerConfig tm);

    boolean pingTenant(TenantManagerConfig tm, String tenantId);

    default void validateConfig(List<TenantLimit> limits) {}
}
