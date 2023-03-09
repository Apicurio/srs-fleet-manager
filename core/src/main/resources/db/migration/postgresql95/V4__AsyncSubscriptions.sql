alter table registry alter column instance_type DROP NOT NULL;
alter table registry add failed_reason varchar(255);
-- Index? create index idx_registry_5 on registry (instance_type);