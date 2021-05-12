package org.bf2.srs.fleetmanager.rest.impl;

import org.bf2.srs.fleetmanager.rest.SchemaResourceV1;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class SchemaResourceV1Impl implements SchemaResourceV1 {

    private static final String SCHEMA;

    static {
        try {
            SCHEMA = new String(SchemaResourceV1Impl.class.getResourceAsStream("/srs-fleet-manager.json").readAllBytes());
        } catch (Exception e) {
            throw new IllegalStateException("Could not load Open API schema for the v1 REST interface.");
        }
    }

    @Override
    public String getSchema() {
        return SCHEMA;
    }
}
