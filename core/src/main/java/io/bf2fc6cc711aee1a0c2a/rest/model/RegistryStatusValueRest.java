
package io.bf2fc6cc711aee1a0c2a.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public enum RegistryStatusValueRest {

    PROVISIONING("PROVISIONING"),
    AVAILABLE("AVAILABLE"),
    UNAVAILABLE("UNAVAILABLE");

    private final String value;

    private final static Map<String, RegistryStatusValueRest> CONSTANTS = new HashMap<>();

    static {
        for (RegistryStatusValueRest c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    RegistryStatusValueRest(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    public static Optional<RegistryStatusValueRest> fromValueOptional(String value) {
        return ofNullable(CONSTANTS.get(value));
    }

    @JsonCreator
    public static RegistryStatusValueRest fromValue(String value) {
        return fromValueOptional(value).orElseThrow();
    }

    @Override
    public String toString() {
        return this.value;
    }
}
