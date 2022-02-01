package org.bf2.srs.fleetmanager.execution.impl.tasks.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExecutionProperties {

    @ConfigProperty(name = "srs-fleet-manager.execution.deprovisioning.deprovision-stuck-instance-timeout-seconds", defaultValue = "3600")
    Integer deprovisionStuckInstanceTimeoutSeconds;

    public Duration getDeprovisionStuckInstanceTimeout() {
        return Duration.ofSeconds(deprovisionStuckInstanceTimeoutSeconds);
    }
}
