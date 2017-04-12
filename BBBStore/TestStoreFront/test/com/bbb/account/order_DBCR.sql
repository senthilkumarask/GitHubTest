drop table bbb_order;
create table bbb_order (
   order_id varchar2(40) not null,
   user_ip varchar2(15) null,
   billing_address_id varchar2(40) null,
   university_id varchar2(40) null,
   SUBSTATUS               VARCHAR2(40),
   SUBSTATUS_DETAIL        VARCHAR2(254),
   school_id varchar2(40) null,
   order_xml CLOB,
   affiliate varchar2(50),
   school_coupon varchar2(40),
   online_order_number varchar2(40),
   bopus_order_number varchar2(40),
   constraint bbb_order_p primary key (order_id) using index tablespace indx01,
   constraint bbb_order_f foreign key (order_id) references dcspp_order(order_id)
)lob(order_xml) store as (tablespace lobdata);


alter table BBB_REGISTRY 
add registry_co_owner VARCHAR2(254) NULL CONSTRAINT bbb_registry_fk2 REFERENCES dps_user(id);
