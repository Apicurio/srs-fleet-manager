package org.bf2.srs.fleetmanager.rest.publicapi.impl;

import org.bf2.srs.fleetmanager.rest.publicapi.ApiResource;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryListRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryRest;
import org.bf2.srs.fleetmanager.rest.service.RegistryService;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ApiResourceImpl implements ApiResource {

    private static final String SCHEMA;

    static {
        try {
            SCHEMA = new String(ApiResourceImpl.class.getResourceAsStream("/srs-fleet-manager.json").readAllBytes());
        } catch (Exception e) {
            throw new IllegalStateException("Could not load Open API schema for the v1 REST interface.", e);
        }
    }

    @Inject
    RegistryService registryService;

    @Inject
    Convert convert;

    @Override
    public RegistryListRest getRegistries(Integer page,
                                          Integer size,
                                          String orderBy, String search) {
        return convert.convert(registryService.getRegistries(page, size, orderBy, search));
    }

    @Override
    public RegistryRest createRegistry(RegistryCreateRest data) throws StorageConflictException {
        return convert.convert(registryService.createRegistry(convert.convert(data)));
    }

    @Override
    public RegistryRest getRegistry(String id) throws RegistryNotFoundException {
        return convert.convert(registryService.getRegistry(id));
    }

    @Override
    public void deleteRegistry(String id) throws StorageConflictException, RegistryNotFoundException {
        registryService.deleteRegistry(id);
    }
}
