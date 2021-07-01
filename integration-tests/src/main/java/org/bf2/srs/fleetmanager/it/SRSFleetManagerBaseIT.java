package org.bf2.srs.fleetmanager.it;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayNameGeneration(SimpleDisplayName.class)
@ExtendWith(DeploymentManager.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SRSFleetManagerBaseIT {

    protected static TestInfraManager infra = TestInfraManager.getInstance();

}
