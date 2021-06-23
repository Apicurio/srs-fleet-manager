
package org.bf2.srs.fleetmanager.rest.privateapi.beans;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RegistryDeploymentStatusValueRest {

    PROCESSING("PROCESSING"),
    AVAILABLE("AVAILABLE"),
    UNAVAILABLE("UNAVAILABLE");
    private final String value;
    private final static Map<String, RegistryDeploymentStatusValueRest> CONSTANTS = new HashMap<String, RegistryDeploymentStatusValueRest>();

    static {
        for (RegistryDeploymentStatusValueRest c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private RegistryDeploymentStatusValueRest(String value) {
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
    public static RegistryDeploymentStatusValueRest fromValue(String value) {
        RegistryDeploymentStatusValueRest constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
