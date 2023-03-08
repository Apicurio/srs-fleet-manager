package org.bf2.srs.fleetmanager.execution.impl.tasks.provision;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bf2.srs.fleetmanager.execution.impl.tasks.AbstractTask;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;

import static java.util.Objects.requireNonNull;
import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.CREATE_SUBSCRIPTION_T;

@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class CreateSubscriptionTask extends AbstractTask {

    private AccountInfo accountInfo;
    private String registryId;

    @Builder
    public CreateSubscriptionTask(String registryId, AccountInfo accountInfo) {
        super(CREATE_SUBSCRIPTION_T);
        requireNonNull(registryId);
        requireNonNull(accountInfo);
        this.registryId = registryId;
        this.accountInfo = accountInfo;
    }
}