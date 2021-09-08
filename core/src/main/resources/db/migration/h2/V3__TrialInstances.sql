alter table registry add instance_type varchar(255);
update registry set instance_type = 'standard';
alter table registry alter column instance_type set not null;
-- Index? create index idx_registry_5 on registry (instance_type);
