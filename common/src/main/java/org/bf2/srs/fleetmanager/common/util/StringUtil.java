package org.bf2.srs.fleetmanager.common.util;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class StringUtil {

    public static String shorten(String str, int maxLen) {
        if (str == null || str.length() <= maxLen)
            return str;
        return str.strip().substring(0, maxLen - 3) + "...";
    }
}
