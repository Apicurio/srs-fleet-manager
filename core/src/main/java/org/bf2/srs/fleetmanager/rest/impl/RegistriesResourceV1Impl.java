package org.bf2.srs.fleetmanager.rest.impl;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import org.bf2.srs.fleetmanager.execution.impl.tasks.ScheduleRegistryTask;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.rest.RegistriesResourceV1;
import org.bf2.srs.fleetmanager.rest.convert.ConvertRegistry;
import org.bf2.srs.fleetmanager.rest.model.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.model.RegistryRest;
import org.bf2.srs.fleetmanager.rest.model.RegistryRestList;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.PanacheRegistryRepository;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.Registry;
import org.bf2.srs.fleetmanager.util.TypeConvertUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
    PanacheRegistryRepository registryRepository;


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
    public RegistryRestList getRegistries(Integer page, Integer size, String orderBy, String search) {
        PanacheQuery<Registry> itemsQuery;
        if (orderBy != null) {
            var order = orderBy.split(" ");
            if (order.length != 2) {
                throw new ValidationException("invalid orderBy");
            }
            if ("asc".equals(order[1])) {
                itemsQuery = this.registryRepository.
                        findAll(Sort.by(order[0], Sort.Direction.Ascending));
            } else {
                itemsQuery = this.registryRepository.
                        findAll(Sort.by(order[0], Sort.Direction.Descending));
            }
        } else {
            itemsQuery = this.registryRepository.
                    findAll(Sort.by("id", Sort.Direction.Ascending));
        }
        var query = itemsQuery;
        page = (page != null) ? page : 10;
        size = (size != null) ? size : 10;
           
        var total = this.registryRepository.count();
        var items = itemsQuery.page(Page.of(page, size)).stream().map(convertRegistry::convert)
                .collect(Collectors
                        .toCollection(ArrayList::new));
        return RegistryRestList.builder().items(items)
                .page(page.toString())
                .size(page.toString())
                .total(String.valueOf(total)).build();
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
