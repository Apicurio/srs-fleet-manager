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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PACKAGE;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Entity
@Table(name = "registrydata")
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class RegistryData {

    /**
     * (Optional when new)
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * (Optional)
     */
    @Column(name = "name")
    private String name;

    /**
     * (Optional*)
     */
    @Column(name = "registryurl", unique = true)
    private String registryUrl;

    /**
     * (Optional*)
     */
    @Column(name = "tenantid", unique = true)
    private String tenantId;

    /**
     * (Optional*)
     */
    @ManyToOne
    @JoinColumn(name = "registrydeployment_id")
    private RegistryDeploymentData registryDeployment;

    /**
     * (Required)
     */
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "status_id")
    @NotNull
    private RegistryStatusData status;

    /**
     * (Required)
     */
    @NotNull
    private String owner;
}
