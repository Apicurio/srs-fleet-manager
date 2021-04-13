package org.bf2.srs.fleetmanager.spi.model;

public class AccountInfo {

    private final String organizationId;

    public AccountInfo(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationId() {
        return organizationId;
    }
}
