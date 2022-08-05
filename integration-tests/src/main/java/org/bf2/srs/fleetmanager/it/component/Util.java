package org.bf2.srs.fleetmanager.it.component;

public class Util {

    public static String getMandatoryEnvVar(String envVar) {
        String var = System.getenv().get(envVar);
        if (var == null || var.isEmpty()) {
            throw new IllegalStateException("missing " + envVar + " env var");
        }
        return var;
    }
}
