create table parse_info (
    id uuid primary key,
    date timestamp not null,
    content text not null
);

create table matchers (
    parse_info_id uuid references parse_info(id),
    id integer not null,
    class_name varchar(255) not null,
    matcher_type varchar(30) not null,
    name varchar(1024) not null,
    unique (parse_info_id, id)
);

create table nodes (
    parse_info_id uuid references parse_info(id),
    id integer not null,
    parent_id integer not null,
    level integer not null,
    success integer not null,
    matcher_id integer not null,
    start_index integer not null,
    end_index integer not null,
    time bigint not null,
    unique (parse_info_id, id)
);

alter table nodes add foreign key (parse_info_id, matcher_id)
    references matchers(parse_info_id, id);
create index nodes_parent_id on nodes(parent_id);
create index nodes_indices on nodes(start_index, end_index);
