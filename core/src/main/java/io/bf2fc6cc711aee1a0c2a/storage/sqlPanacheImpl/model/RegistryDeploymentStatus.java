package io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static lombok.AccessLevel.PACKAGE;

/**
 * NOTE: We are not using a common superclass with {@link io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryStatus},
 * because of more specific validation.
 * <p>
 * TODO: Maybe use enum for `value`, but that would require having intermediate model classes between REST and storage.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class RegistryDeploymentStatus {

    /**
     * (Optional when new)
     */
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * (Required)
     */
    @NotNull
    private Instant lastUpdated;

    /**
     * (Required)
     */
    @NotEmpty
    @Pattern(regexp = "PROCESSING|AVAILABLE|UNAVAILABLE")
    private String value;
}
