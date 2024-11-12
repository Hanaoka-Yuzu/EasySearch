use easy_search;
drop table case_cause_node;
create table case_cause_node
(
    cause_code        int          not null comment '案由代码'
        primary key,
    parent_cause_code int          null comment '上级案由代码',
    cause             varchar(200) not null comment '案由名称',
    cause_category    varchar(50)  null comment '案由类别'
)
    comment '案由层级节点';

