package org.bf2.srs.fleetmanager.it.suites;

import org.bf2.srs.fleetmanager.it.LocalAMSIT;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        LocalAMSIT.class
})
public class LocalAMSSuite {
}
