package io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl;

import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PanacheRegistryDeploymentRepository implements PanacheRepositoryBase<RegistryDeployment, Long> {

}
