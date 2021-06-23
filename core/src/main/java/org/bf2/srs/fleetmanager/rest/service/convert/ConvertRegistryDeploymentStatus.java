package org.bf2.srs.fleetmanager.rest.service.convert;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentStatusValue;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentStatus;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentStatusData;

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

    public RegistryDeploymentStatus convert(@Valid RegistryDeploymentStatusData status) {
        requireNonNull(status);
        return RegistryDeploymentStatus.builder()
                .value(RegistryDeploymentStatusValue.fromValue(status.getValue()))
                .lastUpdated(convertISO8601.convert(status.getLastUpdated()))
                .build();
    }

    public RegistryDeploymentStatusData initial() {
        return RegistryDeploymentStatusData.builder()
                .value(RegistryDeploymentStatusValue.PROCESSING.value())
                .lastUpdated(Instant.now())
                .build();
    }
}
