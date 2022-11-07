package org.bf2.srs.fleetmanager.spi.common.model;

public class AccountInfo {

    private String organizationId;
    private String accountUsername;
    private Long accountId;
    private boolean admin;

    public AccountInfo() {
    }

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

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setAccountUsername(String accountUsername) {
        this.accountUsername = accountUsername;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
