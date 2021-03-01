package org.bf2.srs.fleetmanager.rest.convert;

import org.bf2.srs.fleetmanager.rest.model.RegistryStatusValueRest;
import org.bf2.srs.fleetmanager.rest.model.RegistryStatusRest;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryStatus;

import java.time.Instant;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;

import static java.util.Objects.requireNonNull;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ConvertRegistryStatus {

    @Inject
    ConvertISO8601 convertISO8601;

    public RegistryStatusRest convert(@Valid RegistryStatus status) {
        requireNonNull(status);
        return RegistryStatusRest.builder()
                .value(RegistryStatusValueRest.fromValue(status.getValue()))
                .lastUpdated(convertISO8601.convert(status.getLastUpdated()))
                .build();
    }

    public RegistryStatus initial() {
        return RegistryStatus.builder()
                .value(RegistryStatusValueRest.PROVISIONING.value())
                .lastUpdated(Instant.now())
                .build();
    }
}
