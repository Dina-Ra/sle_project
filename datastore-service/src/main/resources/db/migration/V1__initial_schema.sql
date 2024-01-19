create table matrix
(
    id              bigserial       not null primary key,
    str_matrix      varchar(255)    not null,
    det             varchar(50)     not null,
    reverse         varchar(255)    not null,
    transpose       varchar(255)    not null,
    decomposition   varchar(255)
);
create index idx_matrix_str_matrix on matrix (str_matrix);
