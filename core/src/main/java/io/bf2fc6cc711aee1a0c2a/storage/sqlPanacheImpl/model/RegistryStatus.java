package io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class RegistryStatus extends Status {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;
}
