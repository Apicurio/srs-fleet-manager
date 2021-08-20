
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
public enum RegistryStatusValue {

    ACCEPTED("accepted"),
    PROVISIONING("provisioning"),
    READY("ready"),
    FAILED("failed"),
    REQUESTED_DEPROVISIONING("deprovision"),
    DEPROVISIONING_DELETING("deleting");

    private final String value;

    private static final Map<String, RegistryStatusValue> CONSTANTS = new HashMap<>();

    static {
        for (RegistryStatusValue c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    RegistryStatusValue(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    public static Optional<RegistryStatusValue> ofOptional(String value) {
        return ofNullable(CONSTANTS.get(value));
    }

    @JsonCreator
    public static RegistryStatusValue of(String value) {
        return ofOptional(value).orElseThrow();
    }

    @Override
    public String toString() {
        return this.value;
    }
}
