alter table registry modify column instance_type varchar(255) null;
-- Index? create index idx_registry_5 on registry (instance_type);