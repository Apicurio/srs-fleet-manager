package org.bf2.srs.fleetmanager.rest.publicapi.impl;

import io.quarkus.security.identity.SecurityIdentity;
import lombok.SneakyThrows;
import org.bf2.srs.fleetmanager.rest.publicapi.ApiResource;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryListRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryRest;
import org.bf2.srs.fleetmanager.rest.service.RegistryService;
import org.bf2.srs.fleetmanager.util.SecurityUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ApiResourceImpl implements ApiResource {

    private static final String SCHEMA;
    private static final String OWNER_PLACEHOLDER = "Unauthenticated";

    static {
        try {
            SCHEMA = new String(ApiResourceImpl.class.getResourceAsStream("/srs-fleet-manager.json").readAllBytes());
        } catch (Exception e) {
            throw new IllegalStateException("Could not load Open API schema for the v1 REST interface.");
        }
    }

    @Inject
    RegistryService registryService;

    @Inject
    Convert convert;

    @Inject
    Instance<SecurityIdentity> securityIdentity;

    @ConfigProperty(name = "srs-fleet-manager.username-attribute")
    String usernameAttribute;

    @Override
    public RegistryListRest getRegistries(@Min(0) Integer page,
                                          @Min(1) @Max(1000) Integer size,
                                          String orderBy, String search) {
        return convert.convert(registryService.getRegistries(page, size, orderBy, search));
    }

    @SneakyThrows
    @Override
    public RegistryRest createRegistry(RegistryCreateRest data) {
        String owner = OWNER_PLACEHOLDER;
        if (SecurityUtil.isResolvable(securityIdentity)) {
            owner = securityIdentity.get().getAttribute(usernameAttribute);
        }
        return convert.convert(registryService.createRegistry(convert.convert(data, owner)));
    }

    @SneakyThrows
    @Override
    public RegistryRest getRegistry(String id) {
        return convert.convert(registryService.getRegistry(id));
    }

    @SneakyThrows
    @Override
    public void deleteRegistry(String id) {
        registryService.deleteRegistry(id);
    }

    @Override
    public String getSchema() {
        return SCHEMA;
    }
}
