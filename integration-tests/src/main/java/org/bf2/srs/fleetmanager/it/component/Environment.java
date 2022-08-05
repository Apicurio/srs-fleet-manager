package org.bf2.srs.fleetmanager.it.component;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
@ToString
public class Environment {

    public static Environment create() {
        return new Environment();
    }

    private Map<String, String> envVariables = new HashMap<>();

    public Environment set(String key, String value) {
        if (value != null) {
            envVariables.put(key, value);
        }
        return this;
    }

    public Environment inherit(Environment parent) {
        envVariables.putAll(parent.getEnvVariables());
        return this;
    }

    public Map<String, String> getEnvVariables() {
        return envVariables;
    }
}
