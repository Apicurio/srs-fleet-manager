package io.bf2fc6cc711aee1a0c2a.rest.impl;

import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.ScheduleRegistryTask;
import io.bf2fc6cc711aee1a0c2a.rest.RegistriesResourceV1;
import io.bf2fc6cc711aee1a0c2a.rest.convert.ConvertRegistry;
import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryCreateRest;
import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryRest;
import io.bf2fc6cc711aee1a0c2a.storage.RegistryNotFoundException;
import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;
import io.bf2fc6cc711aee1a0c2a.storage.StorageConflictException;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;

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
    public void deleteRegistry(Long id) throws RegistryNotFoundException {
        storage.deleteRegistry(id);
    }
}
