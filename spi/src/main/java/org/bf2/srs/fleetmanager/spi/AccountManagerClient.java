package org.bf2.srs.fleetmanager.spi;

import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

public interface AccountManagerClient {

    void hasEntitlements(AccountInfo accountInfo);
}
