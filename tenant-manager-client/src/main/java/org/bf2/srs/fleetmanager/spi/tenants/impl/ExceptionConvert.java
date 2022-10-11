package org.bf2.srs.fleetmanager.spi.tenants.impl;

import io.apicurio.tenantmanager.client.exception.ApicurioTenantNotFoundException;
import io.apicurio.tenantmanager.client.exception.TenantManagerClientException;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerServiceException;
import org.bf2.srs.fleetmanager.spi.tenants.TenantNotFoundServiceException;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public class ExceptionConvert {

    public static TenantManagerServiceException convert(TenantManagerClientException ex) {
        return new TenantManagerServiceException(ex.getMessage(), ex);
    }

    public static TenantNotFoundServiceException convert(ApicurioTenantNotFoundException ex) {
        return new TenantNotFoundServiceException(ex.getMessage(), ex);
    }
}
