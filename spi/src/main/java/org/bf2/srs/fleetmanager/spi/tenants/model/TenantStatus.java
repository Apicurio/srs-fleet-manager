package org.bf2.srs.fleetmanager.spi.tenants.model;

import java.util.HashMap;
import java.util.Map;

public enum TenantStatus {

    READY("READY"),
    TO_BE_DELETED("TO_BE_DELETED"),
    DELETED("DELETED");

    private final String value;

    private static final Map<String, TenantStatus> CONSTANTS = new HashMap<>();

    static {
        for (TenantStatus c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private TenantStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String value() {
        return this.value;
    }

    public static TenantStatus fromValue(String value) {
        TenantStatus constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}
