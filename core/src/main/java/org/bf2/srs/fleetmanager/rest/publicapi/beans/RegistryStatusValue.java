
package org.bf2.srs.fleetmanager.rest.publicapi.beans;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RegistryStatusValue {

    accepted("accepted"),
    provisioning("provisioning"),
    ready("ready"),
    failed("failed"),
    deprovision("deprovision"),
    deleting("deleting");
    private final String value;
    private final static Map<String, RegistryStatusValue> CONSTANTS = new HashMap<String, RegistryStatusValue>();

    static {
        for (RegistryStatusValue c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private RegistryStatusValue(String value) {
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
    public static RegistryStatusValue fromValue(String value) {
        RegistryStatusValue constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
