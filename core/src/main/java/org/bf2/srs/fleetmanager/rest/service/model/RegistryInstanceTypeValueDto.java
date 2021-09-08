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
public enum RegistryInstanceTypeValueDto {

    STANDARD("standard"),
    EVAL("eval");

    private final String value;

    private static final Map<String, RegistryInstanceTypeValueDto> CONSTANTS = new HashMap<>();

    static {
        for (RegistryInstanceTypeValueDto c : values()) {
            CONSTANTS.put(c.value, c);
        }
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
