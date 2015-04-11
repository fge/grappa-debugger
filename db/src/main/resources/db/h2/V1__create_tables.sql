create table matchers (
    id integer not null,
    class_name varchar(255) not null,
    matcher_type varchar(30) not null,
    name varchar(1024) not null
);

alter table matchers add primary key(id);

create table nodes (
    id integer not null,
    parent_id integer not null,
    level integer not null,
    success integer not null,
    matcher_id integer not null,
    start_index integer not null,
    end_index integer not null,
    time bigint not null
);

alter table nodes add primary key(id);
alter table nodes add foreign key (matcher_id) references matchers(id);
create index nodes_parent_id on nodes(parent_id);
create index nodes_indices on nodes(start_index, end_index);
