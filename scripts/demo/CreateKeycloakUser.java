///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.keycloak:keycloak-admin-client:11.0.3

import static java.lang.System.out;

import java.lang.System;
import java.util.Arrays;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder.HostnameVerificationPolicy;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

public class CreateKeycloakUser {

    public static void main(String... args) {
        String controlPlaneTenantId = args[0];
        if (controlPlaneTenantId == null) {
            throw new IllegalStateException("Control plane tenant id is mandatory");
        }

        String authserverurl = System.getenv("AUTH_SERVER_URL");
        // String authserverurl = args[1];
        if (authserverurl == null) {
            throw new IllegalStateException("auth server url is mandatory");
        }
        String adminuser = System.getenv("ADMIN_USERNAME");
        // String adminuser = args[2];
        if (adminuser == null) {
            throw new IllegalStateException("admin username is mandatory");
        }
        String adminpassword = System.getenv("ADMIN_PASSWORD");
        // String adminpassword = args[3];
        if (adminpassword == null) {
            throw new IllegalStateException("admin password is mandatory");
        }

        ResteasyClientBuilder builder = new ResteasyClientBuilder();
        builder.setIsTrustSelfSignedCertificates(true);
        builder.hostnameVerification(HostnameVerificationPolicy.ANY);
        builder.disableTrustManager();
        ResteasyClient client = builder.build();

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(authserverurl)
                .realm("master")
                .clientId("admin-cli")
                .grantType("password")
                .username(adminuser)
                .password(adminpassword)
                .resteasyClient(client)
                .build();

        UserRepresentation user = new UserRepresentation();
        user.setUsername("sr-admin-tenant-" + controlPlaneTenantId);
        user.setEmailVerified(true);
        user.setEnabled(true);
        // user.setCredentials(List.of(creds));

        RealmResource realmResource = keycloak.realm("sr-tenant-" + controlPlaneTenantId);

        Response response = realmResource.users().create(user);  
        String userId = CreatedResponseUtil.getCreatedId(response);

        UserResource userResource = realmResource.users().get(userId);

        CredentialRepresentation creds = new CredentialRepresentation();
        creds.setValue("password");
        creds.setTemporary(false);
        creds.setType(CredentialRepresentation.PASSWORD);
        userResource.resetPassword(creds);

        RoleRepresentation sradminRealmRole = realmResource.roles().get("sr-admin").toRepresentation();

        userResource.roles().realmLevel().add(Arrays.asList(sradminRealmRole));
        
        out.println("User " + user.getUsername() + " created");
    }
}
