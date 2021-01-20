package io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@ToString
public abstract class Status {

    private Instant lastUpdated;

    private String status;
}
