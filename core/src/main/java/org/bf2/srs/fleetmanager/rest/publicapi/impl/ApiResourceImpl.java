package org.bf2.srs.fleetmanager.rest.publicapi.impl;

import org.bf2.srs.fleetmanager.rest.publicapi.ApiResource;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Error;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.ErrorList;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryList;
import org.bf2.srs.fleetmanager.rest.service.RegistryService;
import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ApiResourceImpl implements ApiResource {

    @Inject
    RegistryService registryService;

    @Inject
    Convert convert;

    @Override
    public RegistryList getRegistries(Integer page,
                                      Integer size,
                                      String orderBy, String search) {
        return convert.convert(registryService.getRegistries(page, size, orderBy, search));
    }

    @Override
    public Registry createRegistry(RegistryCreate data)
            throws StorageConflictException, TermsRequiredException, ResourceLimitReachedException {
        return convert.convert(registryService.createRegistry(convert.convert(data)));
    }

    @Override
    public Registry getRegistry(String id) throws RegistryNotFoundException {
        return convert.convert(registryService.getRegistry(id));
    }

    @Override
    public void deleteRegistry(String id) throws StorageConflictException, RegistryNotFoundException {
        registryService.deleteRegistry(id);
    }

    @Override
    public ErrorList getErrors() {
        return null;
    }

    @Override
    public Error getError(Integer id) {
        return null;
    }
}
