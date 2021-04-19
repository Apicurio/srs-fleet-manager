package org.bf2.srs.fleetmanager.spi.model;

public class AccountInfo {

    private final String organizationId;
    private final String accountUsername;

    public AccountInfo(String organizationId, String accountUsername) {
        this.organizationId = organizationId;
        this.accountUsername = accountUsername;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getAccountUsername() {
        return accountUsername;
    }
}
