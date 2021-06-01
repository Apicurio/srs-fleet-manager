package org.bf2.srs.fleetmanager.spi.model;

public class AccountInfo {

    private final String organizationId;
    private final String accountUsername;
    private final boolean admin;

    public AccountInfo(String organizationId, String accountUsername, boolean admin) {
        this.organizationId = organizationId;
        this.accountUsername = accountUsername;
        this.admin = admin;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getAccountUsername() {
        return accountUsername;
    }

    public boolean isAdmin() {
        return admin;
    }
}
