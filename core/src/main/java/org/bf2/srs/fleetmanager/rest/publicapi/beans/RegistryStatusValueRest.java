
package org.bf2.srs.fleetmanager.rest.publicapi.beans;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RegistryStatusValueRest {

    PROVISIONING("PROVISIONING"),
    AVAILABLE("AVAILABLE"),
    UNAVAILABLE("UNAVAILABLE");
    private final String value;
    private final static Map<String, RegistryStatusValueRest> CONSTANTS = new HashMap<String, RegistryStatusValueRest>();

    static {
        for (RegistryStatusValueRest c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private RegistryStatusValueRest(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static RegistryStatusValueRest fromValue(String value) {
        RegistryStatusValueRest constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
