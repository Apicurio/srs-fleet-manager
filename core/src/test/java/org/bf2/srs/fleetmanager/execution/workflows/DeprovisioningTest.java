package org.bf2.srs.fleetmanager.execution.workflows;

import io.quarkus.test.junit.QuarkusTest;
import org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision.StartDeprovisionRegistryTask;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.TaskSchedule;
import org.bf2.srs.fleetmanager.operation.OperationContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryInstanceTypeValueDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.util.TestTags;
import org.bf2.srs.fleetmanager.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;
import javax.inject.Inject;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@QuarkusTest
public class DeprovisioningTest {

    @Inject
    TaskManager tasks;

    @Inject
    OperationContext opCtx;

    @Inject
    ResourceStorage storage;

    @Inject
    DeprovisionRegistryTestWorker testWorker;

    @BeforeEach
    void beforeEach() {
        // Activate Operation Context
        if (opCtx.isContextDataLoaded())
            throw new IllegalStateException("Unexpected state: Operation Context is already loaded");
        opCtx.loadNewContextData();
    }

    @Test
    @Tag(TestTags.SLOW)
    void testForcedDeprovisioning() throws RegistryStorageConflictException {
        // Create Registry
        var registry = RegistryData.builder()
                .id(UUID.randomUUID().toString())
                .name("test")
                .instanceType(RegistryInstanceTypeValueDto.STANDARD.value())
                .status(RegistryStatusValueDto.PROVISIONING.value())
                .owner("test_user")
                .ownerId(42L)
                .orgId("test_org")
                .build();
        storage.createOrUpdateRegistry(registry);

        var task = StartDeprovisionRegistryTask.builder()
                .registryId(registry.getId())
                .build();
        // A bit of a hack to prevent retrying until the timeout expires
        task.setSchedule(TaskSchedule.builder().minRetries(0).build());

        tasks.submit(task);

        // Wait until the task finishes, should fail fast
        await().atMost(Duration.ofSeconds(10))
                .until(() -> tasks.getAllTasks().isEmpty());

        // The registry did not get deleted
        assertTrue(storage.getRegistryById(registry.getId()).isPresent());
        assertFalse(testWorker.getHasBeenExecuted().get());

        // Try again, after > 5 seconds
        TestUtil.delay(5500);

        tasks.submit(task);

        await().atMost(Duration.ofSeconds(10))
                .until(() -> tasks.getAllTasks().isEmpty());

        assertFalse(storage.getRegistryById(registry.getId()).isPresent());
        assertTrue(testWorker.getHasBeenExecuted().get());
        testWorker.getHasBeenExecuted().set(false);
    }
}
