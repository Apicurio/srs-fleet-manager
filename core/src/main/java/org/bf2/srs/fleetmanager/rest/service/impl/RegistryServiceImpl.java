package org.bf2.srs.fleetmanager.rest.service.impl;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import org.bf2.srs.fleetmanager.execution.impl.tasks.ScheduleRegistryTask;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.rest.service.RegistryService;
import org.bf2.srs.fleetmanager.rest.service.convert.ConvertRegistry;
import org.bf2.srs.fleetmanager.rest.service.model.Registry;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryList;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.PanacheRegistryRepository;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;

import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ValidationException;

@ApplicationScoped
public class RegistryServiceImpl implements RegistryService {

    @Inject
    TaskManager tasks;

    @Inject
    ResourceStorage storage;

    @Inject
    ConvertRegistry convertRegistry;

    @Inject
    PanacheRegistryRepository registryRepository;

    @Override
    public Registry createRegistry(RegistryCreate registryCreate) throws StorageConflictException {
        RegistryData registryData = convertRegistry.convert(registryCreate);
        storage.createOrUpdateRegistry(registryData);
        tasks.submit(ScheduleRegistryTask.builder().registryId(registryData.getId()).build());
        return convertRegistry.convert(registryData);
    }

    @Override
    public RegistryList getRegistries(Integer page, Integer size, String orderBy, String search) {
        PanacheQuery<RegistryData> itemsQuery;
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
        page = (page != null) ? page : 0;
        size = (size != null) ? size : 10;

        var total = this.registryRepository.count();
        var items = itemsQuery.page(Page.of(page, size)).stream().map(convertRegistry::convert)
                .collect(Collectors
                        .toCollection(ArrayList::new));
        return RegistryList.builder().items(items)
                .page(page)
                .size(size)
                .total(total).build();
    }

    @Override
    public Registry getRegistry(String registryId) throws RegistryNotFoundException {
        try {
            Long id = Long.valueOf(registryId);
            return storage.getRegistryById(id)
                    .map(convertRegistry::convert)
                    .orElseThrow(() -> RegistryNotFoundException.create(id));
        } catch (NumberFormatException ex) {
            throw RegistryNotFoundException.create(registryId);
        }
    }

    @Override
    public void deleteRegistry(String registryId) throws RegistryNotFoundException, StorageConflictException {
        try {
            Long id = Long.valueOf(registryId);
            storage.deleteRegistry(id);
        } catch (NumberFormatException ex) {
            throw RegistryNotFoundException.create(registryId);
        }
    }
}
