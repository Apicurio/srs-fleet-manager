package org.bf2.srs.fleetmanager.storage.sqlPanacheImpl;

import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.Registry;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PanacheRegistryRepository implements PanacheRepositoryBase<Registry, Long> {

}
