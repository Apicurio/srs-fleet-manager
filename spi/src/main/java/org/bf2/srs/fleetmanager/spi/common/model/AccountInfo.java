package org.bf2.srs.fleetmanager.spi.common.model;

public class AccountInfo {

    private final String organizationId;
    private final String accountUsername;
    private final Long accountId;
    private final boolean admin;

    public AccountInfo(String organizationId, String accountUsername, boolean admin, Long accountId) {
        this.organizationId = organizationId;
        this.accountUsername = accountUsername;
        this.accountId = accountId;
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

    public Long getAccountId() {
        return accountId;
    }
}
