/*
 * Copyright 2021 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bf2.srs.fleetmanager.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentStatusValue;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentStatusData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * @author Fabian Martinez
 */
@QuarkusTest
public class ResourceStorageTest {

    @Inject
    ResourceStorage storage;

    @Inject
    EntityManager em;

    @Inject
    UserTransaction transaction;

    @BeforeEach
    void cleanup() {
        storage.getAllRegistries()
            .forEach(d -> {
                try {
                    storage.deleteRegistry(d.getId());
                } catch (RegistryNotFoundException | RegistryStorageConflictException e) {
                    throw new IllegalStateException(e);
                }
            });
        assertEquals(0, storage.getAllRegistries().size());
        storage.getAllRegistryDeployments()
            .forEach(d -> {
                try {
                    storage.deleteRegistryDeployment(d.getId());
                } catch (RegistryDeploymentNotFoundException | RegistryDeploymentStorageConflictException e) {
                    throw new IllegalStateException(e);
                }
            });
        assertEquals(0, storage.getAllRegistryDeployments().size());
    }

    @Test
    public void testUpdateRegistryDeploymentUrl() throws RegistryDeploymentStorageConflictException, RegistryDeploymentNotFoundException {

        var rd = RegistryDeploymentData.builder()
            .name("test")
            .registryDeploymentUrl("badUrl")
            .tenantManagerUrl("tenantmanager")
            .status(RegistryDeploymentStatusData.builder().value(RegistryDeploymentStatusValue.AVAILABLE.value()).build())
            .build();

        boolean created = storage.createOrUpdateRegistryDeployment(rd);

        assertTrue(created);
        assertNotNull(rd.getId());

        var list = storage.getAllRegistryDeployments();

        assertTrue(!list.isEmpty());
        assertEquals(1, list.size());

        var deployment = rd;
        deployment.setRegistryDeploymentUrl("goodUrl");

        created = storage.createOrUpdateRegistryDeployment(deployment);

        assertFalse(created);
        assertEquals("goodUrl", deployment.getRegistryDeploymentUrl());
        assertEquals(rd.getId(), deployment.getId());

        var byid = storage.getRegistryDeploymentById(rd.getId());
        assertEquals("goodUrl", byid.get().getRegistryDeploymentUrl());

        storage.getAllRegistryDeployments().forEach(d -> assertEquals("goodUrl", d.getRegistryDeploymentUrl()));

    }

//    @Test
//    public void testUdateRegistry() throws Exception {
//
//        var reg = RegistryData.builder()
//            .name("test")
//            .id(UUID.randomUUID().toString())
//            .registryUrl("testurl")
//            .orgId("aaa")
//            .build();
//
//        boolean created = storage.createOrUpdateRegistry(reg);
//
//        assertTrue(created);
//
//        var list = storage.getAllRegistries();
//        assertTrue(!list.isEmpty());
//        assertEquals(1, list.size());
//
//        reg.setRegistryUrl("newurl");
//
//        created = storage.createOrUpdateRegistry(reg);
//
//        assertFalse(created);
//        assertEquals("newurl", reg.getRegistryUrl());
//
//        var registry = storage.getRegistryById(reg.getId()).get();
//
//        assertEquals(reg.getId(), registry.getId());
//        assertEquals("newurl", registry.getRegistryUrl());
//    }

}
