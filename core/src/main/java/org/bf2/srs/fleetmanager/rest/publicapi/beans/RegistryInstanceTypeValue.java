
package org.bf2.srs.fleetmanager.rest.publicapi.beans;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RegistryInstanceTypeValue {

    standard("standard"),
    eval("eval");
    private final String value;
    private final static Map<String, RegistryInstanceTypeValue> CONSTANTS = new HashMap<String, RegistryInstanceTypeValue>();

    static {
        for (RegistryInstanceTypeValue c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private RegistryInstanceTypeValue(String value) {
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
    public static RegistryInstanceTypeValue fromValue(String value) {
        RegistryInstanceTypeValue constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
