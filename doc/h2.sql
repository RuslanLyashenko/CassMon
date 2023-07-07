drop table COMPACTING;

create table COMPACTING
(
    ID         int auto_increment,
    IP         varchar(20),
    KEYSPACE   varchar(50),
    TABLENAME  varchar(50),
    STARTSIZE int,
    ENDSIZE   int
);

create index IX_COMPACTING on COMPACTING (IP, KEYSPACE, TABLENAME);