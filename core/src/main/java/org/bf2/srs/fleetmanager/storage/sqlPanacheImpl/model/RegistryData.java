package org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model;

import static lombok.AccessLevel.PACKAGE;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Entity
@Table(name = "registry")
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class RegistryData {

    /**
     * (Required)
     *
     * MUST be generated on insert.
     */
    @Id
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private String id;

    /**
     * (Optional)
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * (Optional*)
     */
    @Column(name = "registryurl", unique = true)
    private String registryUrl;

    /**
     * (Optional*)
     */
    @ManyToOne
    @JoinColumn(name = "registrydeployment_id")
    private RegistryDeploymentData registryDeployment;

    /**
     * (Required)
     */
    @Column(name = "owner")
    private String owner;

    /**
     * (Required)
     */
    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    /**
     * (Required)
     */
    @Column(name = "org_id", nullable = false)
    private String orgId;

    @Column(name = "subscription_id")
    private String subscriptionId;

    @Column(name = "instance_type", nullable = false)
    private String instanceType;
}
