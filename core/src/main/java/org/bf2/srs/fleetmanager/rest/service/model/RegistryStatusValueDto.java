
package org.bf2.srs.fleetmanager.rest.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collections;
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

    private static Map<String, RegistryStatusValueDto> CONSTANTS;

    static {
        Map<String, RegistryStatusValueDto> constants = new HashMap<>();
        for (RegistryStatusValueDto c : values()) {
            constants.put(c.value, c);
        }
        CONSTANTS = Collections.unmodifiableMap(constants);
    }

    /**
     * Returns an unmodifiable mapping from String constants to all instances of this class.
     * The String constants are used to represent the data in a persistence layer.
     */
    public static Map<String, RegistryStatusValueDto> getConstants() {
        return CONSTANTS;
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
