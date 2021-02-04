package io.bf2fc6cc711aee1a0c2a.rest.convert;

import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryDeploymentStatusRest;
import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryDeploymentStatusValueRest;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeploymentStatus;

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
