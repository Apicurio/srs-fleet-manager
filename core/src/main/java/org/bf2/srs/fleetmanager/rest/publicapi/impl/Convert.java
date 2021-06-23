package org.bf2.srs.fleetmanager.rest.publicapi.impl;

import org.bf2.srs.fleetmanager.rest.publicapi.beans.Item;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryListRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValueRest;
import org.bf2.srs.fleetmanager.rest.service.model.Registry;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryList;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValue;

import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Convert {


    public RegistryCreate convert(RegistryCreateRest data) {
        return RegistryCreate.builder().name(data.getName()).build();
    }

    public RegistryStatusValueRest convert(RegistryStatusValue data) {
        switch (data) {
            case PROVISIONING:
                return RegistryStatusValueRest.PROVISIONING;
            case AVAILABLE:
                return RegistryStatusValueRest.AVAILABLE;
            case UNAVAILABLE:
                return RegistryStatusValueRest.UNAVAILABLE;
        }
        throw new IllegalStateException("Unreachable.");
    }

    public RegistryRest convert(Registry data) {
        RegistryRest res = new RegistryRest();
        res.setId(data.getId());
        res.setKind(data.getKind());
        res.setHref("");
        res.setName(data.getName());
        res.setStatus(convert(data.getStatus()));
        res.setRegistryUrl(data.getRegistryUrl());
        res.setRegistryDeploymentId(data.getRegistryDeploymentId().intValue()); // TODO Conversion
        return res;
    }

    public Item convertToItem(Registry data) {
        Item res = new Item();
        res.setId(data.getId());
        res.setHref(data.getHref());
        res.setKind(data.getKind());
        res.setRegistryUrl(data.getRegistryUrl());
        res.setName(data.getName());
        res.setRegistryDeploymentId(data.getRegistryDeploymentId().intValue());
        res.setStatus(convert(data.getStatus()));
        return res;
    }

    public RegistryListRest convert(RegistryList registries) {
        RegistryListRest res = new RegistryListRest();
        res.setKind(registries.getKind());
        res.setPage(registries.getPage());
        res.setSize(registries.getSize());
        res.setTotal(registries.getTotal().intValue()); // TODO Conversion
        res.setItems(registries.getItems().stream().map(this::convertToItem).collect(Collectors.toList()));
        return res;
    }
}
