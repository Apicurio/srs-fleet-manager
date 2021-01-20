package io.bf2fc6cc711aee1a0c2a.rest.convert;

import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryStatusRest;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryStatus;
import lombok.SneakyThrows;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ConvertRegistryStatus {

    @Inject
    ConvertISO8601 convertISO8601;

    @SneakyThrows
    public RegistryStatusRest convert(RegistryStatus status) {
        return RegistryStatusRest.builder()
                .status(status.getStatus())
                .lastUpdated(convertISO8601.convert(status.getLastUpdated()))
                .build();
    }
}
