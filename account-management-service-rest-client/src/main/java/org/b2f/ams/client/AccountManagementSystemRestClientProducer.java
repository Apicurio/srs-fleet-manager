package org.b2f.ams.client;

import io.quarkus.arc.profile.UnlessBuildProfile;
import org.b2f.ams.client.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Collections;

@ApplicationScoped
public class AccountManagementSystemRestClientProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    //TODO inject auth properties and endpoint

    private String endpoint = "localhost:8900";
    private Auth auth = null;


    @UnlessBuildProfile("test")
    @Produces
    @ApplicationScoped
    public AccountManagementSystemRestClient produce() {
        log.info("Using Account Management System REST client.");
        return new AccountManagementSystemRestClient(endpoint, Collections.emptyMap(), auth);
    }
}
