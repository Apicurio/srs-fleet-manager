package org.bf2.srs.fleetmanager.storage.sqlPanacheImpl;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import org.bf2.srs.fleetmanager.operation.logging.Logged;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentStorageConflictException;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;
import org.bf2.srs.fleetmanager.util.SearchQuery;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

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

    @Override
    public PanacheQuery<RegistryData> executeRegistrySearchQuery(SearchQuery query, Sort sort) {
        // Leaking Panache internals to the caller. TODO Maybe refactor?
        return this.registryRepository.find(query.getQuery(), sort, query.getArguments());
    }

    private boolean ownerIdExists(long ownerId) {
        return this.registryRepository.find("select r from registry r where r.ownerId = :ownerId limit 1",
                Parameters.with("ownerId", ownerId))
                .firstResultOptional()
                .isPresent();
    }

    private boolean organisationIdExists(String organisationId) {
        return this.registryRepository.find("select r from registry r where r.orgId = :orgId limit 1",
                Parameters.with("orgId", organisationId))
                .firstResultOptional()
                .isPresent();
    @Override
    public long getRegistryCountTotal() {
        return this.registryRepository.count();
    }

    @Override
    @Transactional
    public Map<String, Long> getRegistryCountPerStatus() {
        var res = new HashMap<String, Long>();
        List<Object[]> queryRes = (List<Object[]>) this.registryRepository.getEntityManager()
                .createQuery("select r.status, count(r) from RegistryData r group by r.status")
                .getResultList();
        for (Object[] qr : queryRes) {
            if (qr.length != 2)
                throw new IllegalStateException("Unexpected number of columns in the result row: " + qr.length);
            res.put((String) qr[0], ((Number) qr[1]).longValue());
        }
        return res;
    }

    @Override
    @Transactional
    public long getRegistryOwnerCount() {
        try {
            return this.registryRepository.getEntityManager()
                    .createQuery("select count(distinct r.ownerId) from RegistryData r", Long.class)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return 0;
        }
    }

    @Override
    @Transactional
    public long getRegistryOrganisationCount() {
        try {
            return this.registryRepository.getEntityManager()
                    .createQuery("select count(distinct r.orgId) from RegistryData r", Long.class)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return 0;
        }
    }
}
