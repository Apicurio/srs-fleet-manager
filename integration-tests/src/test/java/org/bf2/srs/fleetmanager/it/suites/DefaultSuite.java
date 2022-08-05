package org.bf2.srs.fleetmanager.it.suites;

import org.bf2.srs.fleetmanager.it.ApiSecurityIT;
import org.bf2.srs.fleetmanager.it.MetricsIT;
import org.bf2.srs.fleetmanager.it.QuotaPlanIT;
import org.bf2.srs.fleetmanager.it.RegistryDeprovisioningIT;
import org.bf2.srs.fleetmanager.it.RegistryProvisioningIT;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        ApiSecurityIT.class,
        MetricsIT.class,
        QuotaPlanIT.class,
        RegistryDeprovisioningIT.class,
        RegistryProvisioningIT.class
})
public class DefaultSuite {
}
