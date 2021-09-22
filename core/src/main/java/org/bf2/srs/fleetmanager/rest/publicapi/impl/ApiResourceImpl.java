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
import org.bf2.srs.fleetmanager.spi.EvalInstancesNotAllowedException;
import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.TooManyEvalInstancesForUserException;
import org.bf2.srs.fleetmanager.spi.TooManyInstancesException;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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

    @ConfigProperty(name = "srs-fleet-manager.registry.browser-url")
    String browserUrl;

    @Override
    public RegistryList getRegistries(Integer page,
                                      Integer size,
                                      String orderBy, String search) {
        RegistryList registries = convert.convert(registryService.getRegistries(page, size, orderBy, search));
        registries.getItems().forEach(registry -> {
            registry.setBrowserUrl(browserUrl.replace("$TENANT_ID", registry.getId()));
        });
        return registries;
    }

    @Override
    public Registry createRegistry(RegistryCreate data)
            throws RegistryStorageConflictException, TermsRequiredException, ResourceLimitReachedException,
            EvalInstancesNotAllowedException, TooManyEvalInstancesForUserException, TooManyInstancesException {
        Registry registry = convert.convert(registryService.createRegistry(convert.convert(data)));
        registry.setBrowserUrl(browserUrl.replace("$TENANT_ID", registry.getId()));
        return registry;
    }

    @Override
    public Registry getRegistry(String id) throws RegistryNotFoundException {
        Registry registry = convert.convert(registryService.getRegistry(id));
        registry.setBrowserUrl(browserUrl.replace("$TENANT_ID", registry.getId()));
        return registry;
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
