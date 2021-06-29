package org.bf2.srs.fleetmanager;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * @author carles.arnal@redhat.com
 */
@QuarkusMain
public class FleetManagerQuarkusMain {
    public static void main(String... args) {
        Quarkus.run(args);
    }
}
