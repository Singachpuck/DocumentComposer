create table if not exists dataset
(
    id      bigint auto_increment primary key,
    name    varchar(50),
    created timestamp,
    format  char(10),
    bytes   blob,
    size    bigint
);

create table if not exists template
(
    id                       bigint auto_increment primary key,
    name                     varchar(50),
    created                  timestamp,
    format                   char(10),
    bytes                    blob,
    size                     bigint,
    begin_token_placeholder  varchar(10),
    end_token_placeholder    varchar(10),
    begin_escape_placeholder varchar(10),
    end_escape_placeholder   varchar(10)
);

create table if not exists composed_document
(
    id                       bigint auto_increment primary key,
    name                     varchar(50),
    created                  timestamp,
    format                   char(10),
    bytes                    blob,
    size                     bigint,
    template_id              bigint,
    dataset_id               bigint,
    foreign key (template_id) references template(id),
    foreign key (dataset_id) references dataset(id)
);

insert into dataset (id, bytes, created, format, name, size)
values (default, X'0102030405', timestamp with time zone '2023-08-10 14:0:0', 'JSON', 'd1', 1024);
insert into dataset (id, bytes, created, format, name, size)
values (default, X'64656667687B', timestamp with time zone '2022-7-10 10:30:0', 'JSON', 'd2', 2048);
insert into dataset (id, bytes, created, format, name, size)
values (default, X'7F20040D5E03', timestamp with time zone '2023-8-20 2:0:0', 'JSON', 'd3', 4096);

insert into template (id, begin_escape_placeholder, begin_token_placeholder, bytes, created, end_escape_placeholder,
                      end_token_placeholder, format, name, size)
values (default, '\', '${', X'0102030405', timestamp with time zone '2023-08-10 20:0:0', '\', '}', 'DOCX', 't1', 1024);
insert into template (id, begin_escape_placeholder, begin_token_placeholder, bytes, created, end_escape_placeholder,
                      end_token_placeholder, format, name, size)
values (default, '\', '(', X'64656667687B', timestamp with time zone '2023-8-10 15:30:0', '\', ')', 'DOCX', 't2', 2048);
insert into template (id, begin_escape_placeholder, begin_token_placeholder, bytes, created, end_escape_placeholder,
                      end_token_placeholder, format, name, size)
values (default, '\', '#', X'7F20040D5E03', timestamp with time zone '2023-8-10 20:0:0', '\', '#', 'DOCX', 't3', 4096);

insert into composed_document (id, bytes, created, format, name, size, template_id, dataset_id)
values (default, X'7F20040D5E03', timestamp with time zone '2023-08-10 14:0:0', 'DOCX', 'd1', 1024, 3, 1);
insert into composed_document (id, bytes, created, format, name, size, template_id, dataset_id)
values (default, X'0102030405', timestamp with time zone '2022-7-10 10:30:0', 'DOCX', 'd2', 2048, 1, 2);
insert into composed_document (id, bytes, created, format, name, size, template_id, dataset_id)
values (default, X'64656667687B', timestamp with time zone '2023-8-20 2:0:0', 'DOCX', 'd3', 4096, 3, 3);