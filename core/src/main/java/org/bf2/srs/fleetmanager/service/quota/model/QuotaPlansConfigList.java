/*
 * Copyright 2021 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bf2.srs.fleetmanager.service.quota.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PACKAGE;

/**
 * @author Fabian Martinez
 */
@JsonPropertyOrder({
        "reconcile",
        "plans",
        "organizations"
})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class QuotaPlansConfigList {

    /**
     * Nullable - default false
     */
    @JsonProperty("reconcile")
    @JsonPropertyDescription("")
    private Boolean reconcile;

    @JsonProperty("plans")
    @JsonPropertyDescription("")
    @NotNull
    private List<QuotaPlan> plans;

    /**
     * Nullable - default empty
     */
    @JsonProperty("organizations")
    @JsonPropertyDescription("")
    private List<OrganizationAssignment> organizations;
}
