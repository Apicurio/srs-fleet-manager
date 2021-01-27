package io.bf2fc6cc711aee1a0c2a.auth;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@QuarkusTest
@QuarkusTestResource(AuthTestResource.class)
public class AuthServiceTest {

    @Inject
    AuthService authService;

    @Inject
    @ConfigProperty(name = "auth.realm.roles")
    List<String> roles;

    @Test
    public void warmUp() {

    }

    @Test
    public void createResourcesTest() {

        final RealmResource tenantRealmResource = authService.createTenantAuthResources("test-tenant-id");

        tenantRealmResource.roles()
                .list()
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList())
                .containsAll(roles);
    }
}