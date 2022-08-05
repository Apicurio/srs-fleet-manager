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

package org.bf2.srs.fleetmanager.it;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Fabian Martinez
 */
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class AuthConfig {

    private String keycloakUrl;

    private String realm;

    private String clientId;

    private String clientSecret;

    public String getTokenEndpoint() {
        return keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }
}
