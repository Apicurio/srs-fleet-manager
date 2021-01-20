package io.bf2fc6cc711aee1a0c2a;

import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
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
