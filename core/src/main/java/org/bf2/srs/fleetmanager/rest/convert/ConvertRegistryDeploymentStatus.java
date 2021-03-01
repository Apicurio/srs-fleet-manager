package org.bf2.srs.fleetmanager.rest.convert;

import org.bf2.srs.fleetmanager.rest.model.RegistryDeploymentStatusValueRest;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentStatus;
import org.bf2.srs.fleetmanager.rest.model.RegistryDeploymentStatusRest;

import java.time.Instant;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;

import static java.util.Objects.requireNonNull;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ConvertRegistryDeploymentStatus {

    @Inject
    ConvertISO8601 convertISO8601;

    public RegistryDeploymentStatusRest convert(@Valid RegistryDeploymentStatus status) {
        requireNonNull(status);
        return RegistryDeploymentStatusRest.builder()
                .value(RegistryDeploymentStatusValueRest.fromValue(status.getValue()))
                .lastUpdated(convertISO8601.convert(status.getLastUpdated()))
                .build();
    }

    public RegistryDeploymentStatus initial() {
        return RegistryDeploymentStatus.builder()
                .value(RegistryDeploymentStatusValueRest.PROCESSING.value())
                .lastUpdated(Instant.now())
                .build();
    }
}
