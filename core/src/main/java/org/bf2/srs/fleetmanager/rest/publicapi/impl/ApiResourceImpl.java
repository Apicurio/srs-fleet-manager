package org.bf2.srs.fleetmanager.rest.publicapi.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.bf2.srs.fleetmanager.rest.publicapi.ApiResource;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Error;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.ErrorList;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryList;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.ServiceStatus;
import org.bf2.srs.fleetmanager.rest.service.ErrorNotFoundException;
import org.bf2.srs.fleetmanager.rest.service.ErrorService;
import org.bf2.srs.fleetmanager.rest.service.RegistryService;
import org.bf2.srs.fleetmanager.spi.TooManyEvalInstancesForUserException;
import org.bf2.srs.fleetmanager.spi.EvalInstancesNotAllowedException;
import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.TooManyInstancesException;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ApiResourceImpl implements ApiResource {

    @Inject
    RegistryService registryService;

    @Inject
    Convert convert;

    @Inject
    ErrorService errorService;

    @Override
    public RegistryList getRegistries(Integer page,
                                      Integer size,
                                      String orderBy, String search) {
        return convert.convert(registryService.getRegistries(page, size, orderBy, search));
    }

    @Override
    public Registry createRegistry(RegistryCreate data)
            throws RegistryStorageConflictException, TermsRequiredException, ResourceLimitReachedException,
            EvalInstancesNotAllowedException, TooManyEvalInstancesForUserException, TooManyInstancesException {
        return convert.convert(registryService.createRegistry(convert.convert(data)));
    }

    @Override
    public Registry getRegistry(String id) throws RegistryNotFoundException {
        return convert.convert(registryService.getRegistry(id));
    }

    @Override
    public void deleteRegistry(String id) throws RegistryStorageConflictException, RegistryNotFoundException {
        registryService.deleteRegistry(id);
    }

    @Override
    public ErrorList getErrors(Integer page, Integer size) {
        return convert.convert(errorService.getErrors(page, size));
    }

    @Override
    public Error getError(Integer id) throws ErrorNotFoundException {
        return convert.convert(errorService.getError(id));
    }

    @Override
    public ServiceStatus getServiceStatus() {
        return convert.convert(registryService.getServiceStatus());
    }

}
