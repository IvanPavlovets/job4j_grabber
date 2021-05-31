create database grabber;

drop table if exists rabbit;

create table rabbit(
  id serial primary key,
  created_date timestamp
);

select * from rabbit;