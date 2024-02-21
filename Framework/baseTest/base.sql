create database testscal;
\c testscal;
create table emp(
    idemp serial primary key,
    nom varchar(50) not null
);

INSERT INTO emp (nom) VALUES
    ('John Doe'),
    ('Jane Smith'),
    ('Bob Johnson'),
    ('Alice Williams');