package io.bf2fc6cc711aee1a0c2a.storage;

import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;

import java.util.List;
import java.util.Optional;

// TODO Decouple model classes?
public interface ResourceStorage {

    ///// Registry

    boolean createOrUpdateRegistry(Registry registry);

    Optional<Registry> getRegistryById(Long id);

    List<Registry> getAllRegistries();

    void deleteRegistry(Registry registry);

    ///// RegistryDeployment

    boolean createOrUpdateRegistryDeployment(RegistryDeployment rd);

    List<RegistryDeployment> getAllRegistryDeployments();

    Optional<RegistryDeployment> getRegistryDeploymentById(Long id);

    void deleteRegistryDeployment(RegistryDeployment rd);
}
