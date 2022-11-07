alter table registry alter column instance_type DROP NOT NULL;
-- Index? create index idx_registry_5 on registry (instance_type);