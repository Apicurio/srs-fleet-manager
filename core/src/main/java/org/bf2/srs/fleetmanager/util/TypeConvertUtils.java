package org.bf2.srs.fleetmanager.util;

public class TypeConvertUtils {

    public static <T> T getValueOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}
