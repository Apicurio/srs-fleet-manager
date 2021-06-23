package org.bf2.srs.fleetmanager.rest.service.convert;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValue;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryStatusData;

import java.time.Instant;
import javax.enterprise.context.ApplicationScoped;

import static java.util.Objects.requireNonNull;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ConvertRegistryStatus {

    public RegistryStatusData initial() {
        return RegistryStatusData.builder()
                .value(RegistryStatusValue.PROVISIONING.value())
                .lastUpdated(Instant.now())
                .build();
    }
}
