-- RegistryDeploymentStatusData

create table if not exists registrydeploymentstatusdata
(
    id bigserial not null,
    lastupdated timestamp,
    value varchar(255)
);

alter table registrydeploymentstatusdata add constraint pk_registrydeploymentstatusdata primary key (id);

-- RegistryDeploymentData

create table if not exists registrydeploymentdata
(
    id bigserial not null,
    name varchar(255),
    registrydeploymenturl varchar(255),
    tenantmanagerurl varchar(255),
    status_id bigint
);

alter table registrydeploymentdata add constraint pk_registrydeploymentdata primary key (id);
alter table registrydeploymentdata add constraint uk_registrydeploymentdata_1 unique (registrydeploymenturl);
alter table registrydeploymentdata add constraint uk_registrydeploymentdata_2 unique (tenantmanagerurl);
alter table registrydeploymentdata add constraint fk_registrydeploymentdata_1 foreign key (status_id) references registrydeploymentstatusdata (id);

-- RegistryData

create table if not exists registrydata
(
    id bigserial not null,
    name varchar(255),
    registryurl varchar(255),
    tenantid varchar(255),
    registrydeployment_id bigint,
    description varchar(255),
    status varchar(255),
    owner varchar(255),
    created_at timestamp,
    updated_at timestamp
);

alter table registrydata add constraint pk_registrydata primary key (id);
alter table registrydata add constraint uk_registrydata_1 unique (registryurl);
alter table registrydata add constraint uk_registrydata_2 unique (tenantid);
alter table registrydata add constraint fk_registrydata_1 foreign key (registrydeployment_id) references registrydeploymentdata (id);
