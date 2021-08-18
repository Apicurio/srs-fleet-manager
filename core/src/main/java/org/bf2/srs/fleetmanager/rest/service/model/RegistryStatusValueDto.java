
package org.bf2.srs.fleetmanager.rest.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public enum RegistryStatusValueDto {

    ACCEPTED("accepted"),
    PROVISIONING("provisioning"),
    READY("ready"),
    FAILED("failed"),
    REQUESTED_DEPROVISIONING("deprovision"),
    DEPROVISIONING_DELETING("deleting");

    private final String value;

    private static final Map<String, RegistryStatusValueDto> CONSTANTS = new HashMap<>();

    static {
        for (RegistryStatusValueDto c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    RegistryStatusValueDto(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    public static Optional<RegistryStatusValueDto> ofOptional(String value) {
        return ofNullable(CONSTANTS.get(value));
    }

    @JsonCreator
    public static RegistryStatusValueDto of(String value) {
        return ofOptional(value).orElseThrow();
    }

    @Override
    public String toString() {
        return this.value;
    }
}
