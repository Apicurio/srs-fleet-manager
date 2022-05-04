package org.bf2.srs.fleetmanager.spi.tenants.impl;

import io.apicurio.multitenant.client.exception.RegistryTenantNotFoundException;
import io.apicurio.multitenant.client.exception.TenantManagerClientException;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerServiceException;
import org.bf2.srs.fleetmanager.spi.tenants.TenantNotFoundServiceException;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public class ExceptionConvert {

    public static TenantManagerServiceException convert(TenantManagerClientException ex) {
        return new TenantManagerServiceException(ex.getMessage(), ex);
    }

    public static TenantNotFoundServiceException convert(RegistryTenantNotFoundException ex) {
        return new TenantNotFoundServiceException(ex.getMessage(), ex);
    }
}
