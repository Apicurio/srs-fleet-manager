package org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static lombok.AccessLevel.PACKAGE;

/**
 * NOTE: We are not using a common superclass with {@link RegistryStatusData},
 * because of more specific validation.
 * <p>
 * TODO: Maybe use enum for `value`, but that would require having intermediate model classes between REST and storage.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Entity
@Table(name = "registrydeploymentstatusdata")
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class RegistryDeploymentStatusData {

    /**
     * (Optional when new)
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * (Required)
     */
    @NotNull
    @Column(name = "lastupdated")
    private Instant lastUpdated;

    /**
     * (Required)
     */
    @Column(name = "value")
    @NotEmpty
    @Pattern(regexp = "PROCESSING|AVAILABLE|UNAVAILABLE") // TODO Move to service layer
    private String value;
}
