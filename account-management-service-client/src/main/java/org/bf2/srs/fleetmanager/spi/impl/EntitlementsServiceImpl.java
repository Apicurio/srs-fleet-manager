package org.bf2.srs.fleetmanager.spi.impl;

import org.b2f.ams.client.AccountManagementSystemRestClient;
import org.bf2.srs.fleetmanager.spi.EntitlementsService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

public class EntitlementsServiceImpl implements EntitlementsService {

    private final AccountManagementSystemRestClient restClient;

    public EntitlementsServiceImpl(AccountManagementSystemRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public boolean hasEntitlements(AccountInfo accountInfo) {


        //TODO perform terms review call for requested resource and then,
        //if accepted perform access review call using rest client

        return true;
    }
}
