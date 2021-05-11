package org.bf2.srs.fleetmanager.rest.impl;

import org.bf2.srs.fleetmanager.execution.impl.tasks.ScheduleRegistryTask;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.rest.RegistriesResourceV1;
import org.bf2.srs.fleetmanager.rest.convert.ConvertRegistry;
import org.bf2.srs.fleetmanager.rest.model.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.model.RegistryRest;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.Registry;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static java.util.stream.Collectors.toList;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class RegistriesResourceV1Impl implements RegistriesResourceV1 {

    @Inject
    TaskManager tasks;

    @Inject
    ResourceStorage storage;

    @Inject
    ConvertRegistry convertRegistry;

    @Override
    public Response createRegistry(RegistryCreateRest registryCreate) throws StorageConflictException {
        Registry registry = convertRegistry.convert(registryCreate);
        storage.createOrUpdateRegistry(registry);
        tasks.submit(ScheduleRegistryTask.builder().registryId(registry.getId()).build());
        var r = convertRegistry.convert(registry);
        return Response.accepted(r).build();

    }

    @Override
    public List<RegistryRest> getRegistries() {
        return storage.getAllRegistries().stream()
                .map(convertRegistry::convert)
                .collect(toList());
    }

    @Override
    public RegistryRest getRegistry(Long id) throws RegistryNotFoundException {
        return storage.getRegistryById(id)
                .map(convertRegistry::convert)
                .orElseThrow(() -> RegistryNotFoundException.create(id));
    }

    @Override
    public void deleteRegistry(Long id) throws RegistryNotFoundException, StorageConflictException {
        storage.deleteRegistry(id);
    }
}
