package org.b2f.ams.client;

import io.quarkus.arc.profile.UnlessBuildProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Collections;

@ApplicationScoped
public class AccountManagementSystemRestClientProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @UnlessBuildProfile("test")
    @Produces
    @ApplicationScoped
    public AccountManagementSystemRestClient produce() {
        log.info("Using Account Management System REST client.");
        return new AccountManagementSystemRestClient(Collections.emptyMap());
    }
}
