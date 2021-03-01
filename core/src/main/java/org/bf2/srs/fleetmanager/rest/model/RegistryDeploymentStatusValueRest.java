
package org.bf2.srs.fleetmanager.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public enum RegistryDeploymentStatusValueRest {

    PROCESSING("PROCESSING"),
    AVAILABLE("AVAILABLE"),
    UNAVAILABLE("UNAVAILABLE");

    private final String value;

    private final static Map<String, RegistryDeploymentStatusValueRest> CONSTANTS = new HashMap<>();

    static {
        for (RegistryDeploymentStatusValueRest c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    RegistryDeploymentStatusValueRest(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    public static Optional<RegistryDeploymentStatusValueRest> fromValueOptional(String value) {
        return ofNullable(CONSTANTS.get(value));
    }

    @JsonCreator
    public static RegistryDeploymentStatusValueRest fromValue(String value) {
        return fromValueOptional(value).orElseThrow();
    }

    @Override
    public String toString() {
        return this.value;
    }
}
