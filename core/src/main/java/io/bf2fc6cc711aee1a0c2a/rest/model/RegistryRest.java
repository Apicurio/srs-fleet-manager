package io.bf2fc6cc711aee1a0c2a.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RegistryRest {

    private Long id;

    private String name;

    private String appUrl;

    private RegistryStatusRest status;
}
