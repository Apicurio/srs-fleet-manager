package io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class RegistryDeployment {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    private String tenantManagerUrl;

    @OneToOne(cascade = {CascadeType.ALL})
    private RegistryDeploymentStatus status;
}
