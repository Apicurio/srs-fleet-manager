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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
public class RegistryData {

    /**
     * (Optional when new)
     */
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * (Optional)
     */
    private String name;

    /**
     * (Optional*)
     */
    @Column(unique = true)
    private String registryUrl;

    /**
     * (Optional*)
     */
    @Column(unique = true)
    private String tenantId;

    /**
     * (Optional*)
     */
    @ManyToOne
    private RegistryDeploymentData registryDeployment;

    /**
     * (Required)
     */
    @OneToOne(cascade = {CascadeType.ALL})
    @NotNull
    private RegistryStatusData status;

    /**
     * (Required)
     */
    @NotNull
    private String owner;
}
