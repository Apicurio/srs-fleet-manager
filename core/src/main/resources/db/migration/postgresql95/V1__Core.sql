-- Registry Deployment Status

create table if not exists registrydeploymentstatus
(
    id bigserial not null,
    lastupdated timestamp,
    value varchar(255)
);

alter table registrydeploymentstatus add constraint pk_registrydeploymentstatus primary key (id);

-- Registry Deployment

create table if not exists registrydeployment
(
    id bigserial not null,
    name varchar(255),
    registrydeploymenturl varchar(255),
    tenantmanagerurl varchar(255),
    status_id bigint
);

alter table registrydeployment add constraint pk_registrydeployment primary key (id);
alter table registrydeployment add constraint uk_registrydeployment_1 unique (registrydeploymenturl);
alter table registrydeployment add constraint uk_registrydeployment_2 unique (tenantmanagerurl);
alter table registrydeployment add constraint uk_registrydeployment_3 unique (name);
alter table registrydeployment add constraint fk_registrydeployment_1 foreign key (status_id) references registrydeploymentstatus (id);

-- Registry

create table if not exists registry
(
    id bigserial not null,
    name varchar(255) not null,
    registryurl varchar(255),
    tenantid varchar(255),
    registrydeployment_id bigint,
    description varchar(255),
    status varchar(255),
    owner varchar(255),
    created_at timestamp,
    updated_at timestamp,
    org_id varchar(255) not null,
    owner_id bigserial not null,
    subscription_id varchar(255)
);

alter table registry add constraint pk_registry primary key (id);
alter table registry add constraint uk_registry_1 unique (registryurl);
alter table registry add constraint uk_registry_2 unique (tenantid);
alter table registry add constraint uk_registry_3 unique (org_id, name);
alter table registry add constraint fk_registry_1 foreign key (registrydeployment_id) references registrydeployment (id);
