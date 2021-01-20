package io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl;

import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PanacheRegistryRepository implements PanacheRepositoryBase<Registry, Long> {

}
