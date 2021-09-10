/*
 * Copyright 2021 Red Hat Inc
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

package org.bf2.srs.fleetmanager.spi.impl;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * @author eric.wittmann@gmail.com
 */
@ApplicationScoped
public class AccountManagementServiceProperties {

    @ConfigProperty(name = "srs-fleet-manager.ams.terms.mas-site-code", defaultValue = "ocm")
    String termsSiteCode;
    @ConfigProperty(name = "srs-fleet-manager.ams.terms.mas-event-code", defaultValue = "onlineService")
    String termsEventCode;

    @ConfigProperty(name = "srs-fleet-manager.ams.resources.resource-type")
    String resourceType;

    @ConfigProperty(name = "srs-fleet-manager.ams.resources.standard.name")
    String standardResourceName;
    @ConfigProperty(name = "srs-fleet-manager.ams.resources.standard.product-id")
    String standardProductId;

    @ConfigProperty(name = "srs-fleet-manager.ams.resources.eval.name")
    String evalResourceName;
    @ConfigProperty(name = "srs-fleet-manager.ams.resources.eval.product-id")
    String evalProductId;


}
