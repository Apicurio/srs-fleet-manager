package org.bf2.srs.fleetmanager.rest.publicapi.impl;

import org.bf2.srs.fleetmanager.auth.NotAuthorizedException;
import org.bf2.srs.fleetmanager.common.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.common.storage.RegistryStorageConflictException;
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
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.bf2.srs.fleetmanager.spi.ams.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.ams.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.common.EvalInstancesNotAllowedException;
import org.bf2.srs.fleetmanager.spi.common.TooManyEvalInstancesForUserException;
import org.bf2.srs.fleetmanager.spi.common.TooManyInstancesException;

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
            EvalInstancesNotAllowedException, TooManyEvalInstancesForUserException, TooManyInstancesException,
            AccountManagementServiceException {
        return convert.convert(registryService.createRegistry(convert.convert(data)));
    }

    @Override
    public Registry getRegistry(String id) throws RegistryNotFoundException, NotAuthorizedException {
        return convert.convert(registryService.getRegistry(id));
    }

    @Override
    public void deleteRegistry(String id) throws RegistryStorageConflictException, RegistryNotFoundException, NotAuthorizedException {
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
