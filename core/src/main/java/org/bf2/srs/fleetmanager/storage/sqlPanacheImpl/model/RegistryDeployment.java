package org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PACKAGE;

/**
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
public class RegistryDeployment {

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
    @Column(unique = true)
    @NotEmpty
    private String tenantManagerUrl;

    /**
     * (Required)
     */
    @Column(unique = true)
    @NotEmpty
    private String registryDeploymentUrl;

    /**
     * (Required)
     */
    @OneToOne(cascade = {CascadeType.ALL})
    @NotNull
    private RegistryDeploymentStatus status;

    /**
     * (Optional)
     */
    private String name;
}
