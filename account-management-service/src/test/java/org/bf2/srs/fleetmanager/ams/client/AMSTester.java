/*
 * Copyright 2021 Red Hat Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bf2.srs.fleetmanager.ams.client;

import java.util.Collections;

import org.bf2.srs.fleetmanager.spi.impl.AccountManagementSystemRestClient;
import org.bf2.srs.fleetmanager.spi.impl.model.response.Organization;

import io.apicurio.rest.client.auth.OidcAuth;

/**
 * @author eric.wittmann@gmail.com
 */
public class AMSTester {

    public static void main(String[] args) {
        String endpoint = "https://api.stage.openshift.com";
        String ssoTokenEndpoint = "https://sso.redhat.com/auth/realms/redhat-external/protocol/openid-connect/token";
        String ssoClientId = "srs-fleet-manager";
        String ssoClientSecret = "500d9cc1-778a-4da2-b371-605bbc374b8a";

        final OidcAuth auth = new OidcAuth(ssoTokenEndpoint, ssoClientId, ssoClientSecret);
        AccountManagementSystemRestClient restClient = new AccountManagementSystemRestClient(endpoint, Collections.emptyMap(), auth);
//
//        TermsReview termsReview = new TermsReview();
//        termsReview.setAccountUsername("ewittman_kafka_registry");
//        termsReview.setEventCode("onlineService");
//        termsReview.setSiteCode("ocm");
//        ResponseTermsReview review = restClient.termsReview(termsReview);
//
//        System.out.println(review);

        String externalOrgId = "14221005";

        Organization organization = restClient.getOrganizationByExternalId(externalOrgId);
        System.out.println(organization);

        System.out.println(restClient.getQuotaCostList(organization.getId(), true));
    }

}
