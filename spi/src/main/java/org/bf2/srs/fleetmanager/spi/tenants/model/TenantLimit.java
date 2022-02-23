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

package org.bf2.srs.fleetmanager.spi.tenants.model;

import static lombok.AccessLevel.PACKAGE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Fabian Martinez
 */
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE)
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class TenantLimit {

    /**
     * Unique key that identifies the resource that is limited up to the specified limit for a tenant
     */
    String type;

    /**
     * Upper bound to apply a specific limit
     */
    Long limit;

}
