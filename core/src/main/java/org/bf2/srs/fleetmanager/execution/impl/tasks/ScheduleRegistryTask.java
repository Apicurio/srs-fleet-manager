package org.bf2.srs.fleetmanager.execution.impl.tasks;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryData;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;

import static java.util.Objects.requireNonNull;
import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.SCHEDULE_REGISTRY_T;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class ScheduleRegistryTask extends AbstractTask {

    private AccountInfo accountInfo;
    private RegistryData registryData;

    @Builder
    public ScheduleRegistryTask(RegistryData registryData, AccountInfo accountInfo) {
        super(SCHEDULE_REGISTRY_T);
        requireNonNull(registryData);
        requireNonNull(accountInfo);
        this.registryData = registryData;
        this.accountInfo = accountInfo;
    }
}
