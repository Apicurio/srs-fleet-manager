package org.bf2.srs.fleetmanager.rest.publicapi.impl;

import org.bf2.srs.fleetmanager.rest.publicapi.SchemaResource;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class SchemaResourceImpl implements SchemaResource {

    private static final String SCHEMA;

    static {
        try {
            SCHEMA = new String(SchemaResourceImpl.class.getResourceAsStream("/srs-fleet-manager.json").readAllBytes());
        } catch (Exception e) {
            throw new IllegalStateException("Could not load Open API schema for the v1 REST interface.");
        }
    }

    @Override
    public String getSchema() {
        return SCHEMA;
    }
}
