SET ECHO ON;
SET DEFINE OFF;

DROP TABLE BBB_CORE.TBS_GS_ORDER_INFO; 

alter table BBB_CORE.bbb_order remove tbs_store_no varchar2(20) null;

drop table BBB_CORE.TBS_STORE_IP_RANGE;