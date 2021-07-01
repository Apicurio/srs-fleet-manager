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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PACKAGE;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Entity
@Table(name = "registrydeploymentdata")
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class RegistryDeploymentData {

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
    @Column(name = "tenantmanagerurl", unique = true)
    @NotEmpty
    private String tenantManagerUrl;

    /**
     * (Required)
     */
    @Column(name = "registrydeploymenturl", unique = true)
    @NotEmpty
    private String registryDeploymentUrl;

    /**
     * (Required)
     */
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "status_id")
    @NotNull
    private RegistryDeploymentStatusData status;

    /**
     * (Optional)
     */
    @Column(name = "name", unique = true)
    @NotEmpty
    private String name;
}
