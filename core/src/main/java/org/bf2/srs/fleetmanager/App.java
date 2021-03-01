package org.bf2.srs.fleetmanager;

import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class App {

    @Inject
    TaskManager taskManager;

    void onStart(@Observes StartupEvent ev) {
        taskManager.start();
    }

    void onStop(@Observes ShutdownEvent ev) {
        taskManager.stop();
    }
}
