package org.bf2.srs.fleetmanager.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public String extractOrganizationId() {

        log.debug("Extracting organization id from the authentication token");
        return null;
    }
}
