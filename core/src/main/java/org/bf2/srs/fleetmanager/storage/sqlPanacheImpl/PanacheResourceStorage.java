package org.bf2.srs.fleetmanager.storage.sqlPanacheImpl;

import io.quarkus.panache.common.Sort;
import org.bf2.srs.fleetmanager.common.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.common.storage.RegistryDeploymentStorageConflictException;
import org.bf2.srs.fleetmanager.common.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.common.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryData;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryDeploymentData;
import org.bf2.srs.fleetmanager.common.storage.util.QueryConfig;
import org.bf2.srs.fleetmanager.common.storage.util.QueryResult;
import org.bf2.srs.fleetmanager.common.storage.util.SearchQuery;
import org.bf2.srs.fleetmanager.operation.logging.Logged;
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
import javax.validation.constraints.NotEmpty;

import static io.quarkus.panache.common.Sort.Direction;
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
                throw new RegistryDeploymentNotFoundException();
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
                .orElseThrow(RegistryDeploymentNotFoundException::new);
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
    public QueryResult<RegistryData> executeRegistrySearchQuery(SearchQuery query, QueryConfig config) {
        var panacheSort = Sort.by(config.getSortColumn(),
                config.getSortDirection() == QueryConfig.Direction.ASCENDING ? Direction.Ascending
                        : Direction.Descending
        );
        var res = this.registryRepository.find(query.getQuery(), panacheSort, query.getArguments())
                .page(config.getIndex(), config.getSize());
        return QueryResult.<RegistryData>builder()
                .items(res.list())
                .count(res.count())
                .build();
    }

    @Override
    public long getRegistryCountTotal() {
        return this.registryRepository.count();
    }

    @Override
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
    public long getRegistryCountPerOrgId(@NotEmpty String orgId) {
        return registryRepository.count("orgId", orgId);
    }

    @Override
    public Map<String, Long> getRegistryCountPerType() {
        var res = new HashMap<String, Long>();
        List<Object[]> queryRes = (List<Object[]>) this.registryRepository.getEntityManager()
                .createQuery("select r.instanceType, count(r) from RegistryData r group by r.instanceType")
                .getResultList();
        for (Object[] qr : queryRes) {
            if (qr.length != 2)
                throw new IllegalStateException("Unexpected number of columns in the result row: " + qr.length);
            res.put((String) qr[0], ((Number) qr[1]).longValue());
        }
        return res;
    }

    @Override
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
    public long getRegistryOrganisationCount() {
        try {
            return this.registryRepository.getEntityManager()
                    .createQuery("select count(distinct r.orgId) from RegistryData r", Long.class)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return 0;
        }
    }

    @Override
    public long getRegistryCountPerDeploymentId(long deploymentId) {
        try {
            return this.registryRepository.getEntityManager()
                    .createQuery("select count(r) from RegistryData r, RegistryDeploymentData d where " +
                            "r.registryDeployment = d and d.id = :deploymentId", Long.class)
                    .setParameter("deploymentId", deploymentId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return 0;
        }
    }

    @Override
    public Optional<RegistryDeploymentData> getRegistryDeploymentByName(String name) {
        return deploymentRepository.find("name", name).singleResultOptional();
    }
}
