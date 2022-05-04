package org.bf2.srs.fleetmanager.spi.tenants;

import org.bf2.srs.fleetmanager.spi.tenants.model.Tenant;
import org.bf2.srs.fleetmanager.spi.tenants.model.TenantLimit;
import org.bf2.srs.fleetmanager.spi.tenants.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.spi.tenants.model.CreateTenantRequest;
import org.bf2.srs.fleetmanager.spi.tenants.model.UpdateTenantRequest;

import java.util.List;
import java.util.Optional;

public interface TenantManagerService {

    Tenant createTenant(TenantManagerConfig tm, CreateTenantRequest tenantRequest) throws TenantManagerServiceException;

    Optional<Tenant> getTenantById(TenantManagerConfig tm, String tenantId) throws TenantManagerServiceException;

    /**
     * This operation is costly. Do not use unless required.
     */
    List<Tenant> getAllTenants(TenantManagerConfig tm) throws TenantManagerServiceException;

    void updateTenant(TenantManagerConfig tm, UpdateTenantRequest req) throws TenantNotFoundServiceException, TenantManagerServiceException;

    /**
     * This operation sets tenant status to TO_BE_DELETED.
     */
    void deleteTenant(TenantManagerConfig tm, String tenantId) throws TenantNotFoundServiceException, TenantManagerServiceException;

    /////

    boolean pingTenantManager(TenantManagerConfig tm);

    boolean pingTenant(TenantManagerConfig tm, String tenantId);

    default void validateConfig(List<TenantLimit> limits) {}
}
