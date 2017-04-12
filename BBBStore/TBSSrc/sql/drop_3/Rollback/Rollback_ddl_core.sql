SET ECHO ON;
SET DEFINE OFF;

ALTER TABLE BBB_CORE.TBS_ITEM_INFO_DETAILS drop column tbs_override_quantity;

drop table BBB_CORE.TBS_GS_ORDER_INFO;

alter table BBB_CORE.bbb_order drop column tbs_store_no;

commit;