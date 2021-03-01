package org.bf2.srs.fleetmanager.storage.sqlPanacheImpl;

import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeployment;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PanacheRegistryDeploymentRepository implements PanacheRepositoryBase<RegistryDeployment, Long> {

}
