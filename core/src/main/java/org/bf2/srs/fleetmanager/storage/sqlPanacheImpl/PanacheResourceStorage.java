package org.bf2.srs.fleetmanager.storage.sqlPanacheImpl;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import org.bf2.srs.fleetmanager.logging.Logged;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentStorageConflictException;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 * @author Fabian Martinez
 */
@ApplicationScoped
@Transactional
@Logged
public class PanacheResourceStorage implements ResourceStorage {

    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    PanacheRegistryRepository registryRepository;

    @Inject
    PanacheRegistryDeploymentRepository deploymentRepository;

    @Inject
    EntityManager em;

    @Override
    public boolean createOrUpdateRegistry(RegistryData registry) throws RegistryStorageConflictException {
        requireNonNull(registry);
        Optional<RegistryData> existing = empty();
        if (registry.getId() != null) {
            //TODO investigate using locks, such as optimistic locks
            existing = registryRepository.findByIdOptional(registry.getId());
        }
        try {
            final Instant now = Instant.now();
            if (existing.isEmpty()) {
                registry.setCreatedAt(now);
            }
            registry.setUpdatedAt(now);
            registryRepository.persistAndFlush(registry);
        } catch (PersistenceException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                throw new RegistryStorageConflictException();
            } else {
                throw ex;
            }
        }
        return existing.isEmpty();
    }

    @Override
    public Optional<RegistryData> getRegistryById(String id) {
        requireNonNull(id);
        return ofNullable(registryRepository.findById(id));
    }

    @Override
    public List<RegistryData> getAllRegistries() {
        return registryRepository.listAll();
    }

    @Override
    public List<RegistryData> getRegistriesByOwner(String owner) {
        return registryRepository.list("owner", owner);
    }

    @Override
    public void deleteRegistry(String id) throws RegistryNotFoundException, RegistryStorageConflictException {
        RegistryData registry = getRegistryById(id)
                .orElseThrow(() -> new RegistryNotFoundException(id));
        try {
            registryRepository.delete(registry);
        } catch (PersistenceException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                throw new RegistryStorageConflictException();
            } else {
                throw ex;
            }
        }
    }

    //*** RegistryDeployment

    @Override
    public boolean createOrUpdateRegistryDeployment(RegistryDeploymentData deployment) throws RegistryDeploymentStorageConflictException, RegistryDeploymentNotFoundException {
        requireNonNull(deployment); // TODO Is this necessary if using @Valid?
        Optional<RegistryDeploymentData> existing = empty();
        if (deployment.getId() != null) {
            //TODO investigate using locks, such as optimistic locks
            existing = deploymentRepository.findByIdOptional(deployment.getId());
            if (existing.isEmpty()) {
                throw new RegistryDeploymentNotFoundException(deployment.getId().toString());
            }
        }
        try {
            final Instant now = Instant.now();
            deployment.getStatus().setLastUpdated(now);

            if (existing.isEmpty()) {
                deploymentRepository.persistAndFlush(deployment);
            } else {
                em.merge(deployment);
                em.flush();
            }

        } catch (PersistenceException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                throw new RegistryDeploymentStorageConflictException();
            } else {
                throw ex;
            }
        }
        return existing.isEmpty();
    }

    @Override
    public List<RegistryDeploymentData> getAllRegistryDeployments() {
        return deploymentRepository.listAll();
    }

    @Override
    public Optional<RegistryDeploymentData> getRegistryDeploymentById(Long id) {
        requireNonNull(id);
        return ofNullable(deploymentRepository.findById(id));
    }

    @Override
    public void deleteRegistryDeployment(Long id) throws RegistryDeploymentNotFoundException, RegistryDeploymentStorageConflictException {
        RegistryDeploymentData rd = getRegistryDeploymentById(id)
                .orElseThrow(() -> new RegistryDeploymentNotFoundException(id.toString()));
        try {
            deploymentRepository.delete(rd);
        } catch (PersistenceException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                throw new RegistryDeploymentStorageConflictException();
            } else {
                throw ex;
            }
        }
    }
}
