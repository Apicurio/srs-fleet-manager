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
public enum RegistryInstanceTypeValueDto {

    STANDARD("standard"),
    EVAL("eval");

    private final String value;

    private static Map<String, RegistryInstanceTypeValueDto> CONSTANTS;

    static {
        var constants = new HashMap<String, RegistryInstanceTypeValueDto>();
        for (RegistryInstanceTypeValueDto c : values()) {
            constants.put(c.value, c);
        }
        CONSTANTS = Collections.unmodifiableMap(constants);
    }

    /**
     * Returns an unmodifiable mapping from String constants to all instances of this class.
     * The String constants are used to represent the data in a persistence layer.
     */
    public static Map<String, RegistryInstanceTypeValueDto> getConstants() {
        return CONSTANTS;
    }

    RegistryInstanceTypeValueDto(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    public static Optional<RegistryInstanceTypeValueDto> ofOptional(String value) {
        return ofNullable(CONSTANTS.get(value));
    }

    @JsonCreator
    public static RegistryInstanceTypeValueDto of(String value) {
        return ofOptional(value).orElseThrow();
    }

    @Override
    public String toString() {
        return this.value;
    }
}
