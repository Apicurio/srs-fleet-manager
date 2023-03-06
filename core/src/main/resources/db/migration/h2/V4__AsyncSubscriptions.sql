alter table registry alter column INSTANCE_TYPE DROP NOT NULL;
-- Index? create index idx_registry_5 on registry (instance_type);