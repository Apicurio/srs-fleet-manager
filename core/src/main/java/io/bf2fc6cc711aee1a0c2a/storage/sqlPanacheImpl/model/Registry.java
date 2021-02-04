package io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model;

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
public class Registry {

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
    private RegistryDeployment registryDeployment;

    /**
     * (Required)
     */
    @OneToOne(cascade = {CascadeType.ALL})
    @NotNull
    private RegistryStatus status;
}
