package io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl;

import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;

import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static java.util.Objects.requireNonNull;

@ApplicationScoped
@Transactional
public class PanacheResourceStorage implements ResourceStorage {

    @Inject
    PanacheRegistryRepository registryRepository;

    @Inject
    PanacheRegistryDeploymentRepository rdRepository;

    @Override
    public boolean createOrUpdateRegistry(Registry registry) {
        requireNonNull(registry);
        Optional<Registry> existing = Optional.empty();
        if (registry.getId() != null)
            existing = registryRepository.findByIdOptional(registry.getId());
        registryRepository.persistAndFlush(registry);
        return existing.isEmpty();
    }

    @Override
    public Optional<Registry> getRegistryById(Long id) {
        requireNonNull(id);
        return Optional.of(registryRepository.findById(id));
    }

    @Override
    public List<Registry> getAllRegistries() {
        return registryRepository.listAll();
    }

    @Override
    public void deleteRegistry(Registry registry) {
        requireNonNull(registry);
        registryRepository.delete(registry);
    }

    // RegistryDeployment

    @Override
    public boolean createOrUpdateRegistryDeployment(RegistryDeployment rd) {
        requireNonNull(rd);
        Optional<RegistryDeployment> existing = Optional.empty();
        if (rd.getId() != null)
            existing = rdRepository.findByIdOptional(rd.getId());
        rdRepository.persistAndFlush(rd);
        return existing.isEmpty();
    }

    @Override
    public List<RegistryDeployment> getAllRegistryDeployments() {
        return rdRepository.listAll();
    }

    @Override
    public Optional<RegistryDeployment> getRegistryDeploymentById(Long id) {
        requireNonNull(id);
        return Optional.of(rdRepository.findById(id));
    }

    @Override
    public void deleteRegistryDeployment(RegistryDeployment rd) {
        requireNonNull(rd);
        rdRepository.delete(rd);
    }

}
