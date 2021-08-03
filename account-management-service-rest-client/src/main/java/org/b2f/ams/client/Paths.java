package org.b2f.ams.client;

public class Paths {

    public static final String AUTHORIZATIONS = "api/authorizations/v1";
    public static final String ACCOUNTS_MANAGEMENT = "api/accounts_mgmt/v1";
    public static final String TERMS_REVIEW_PATH = AUTHORIZATIONS + "/terms_review";
    public static final String CLUSTER_AUTHORIZATION = ACCOUNTS_MANAGEMENT + "/cluster_authorizations";
    public static final String SUBSCRIPTIONS = ACCOUNTS_MANAGEMENT + "/subscriptions/%s";
}
