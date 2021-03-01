package org.bf2.srs.fleetmanager.util;

import static java.lang.System.currentTimeMillis;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class TestUtil {

    public static void delay(long millis) {
        long diff = millis;
        long end = currentTimeMillis() + diff;
        while (diff > 0) {
            try {
                Thread.sleep(diff);
            } catch (InterruptedException e) {
                // NOOP
            }
            long now = currentTimeMillis();
            diff = end - now;
        }
    }
}
