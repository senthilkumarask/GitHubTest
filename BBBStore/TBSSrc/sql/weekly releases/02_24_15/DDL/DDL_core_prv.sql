SET ECHO ON;
SET DEFINE OFF;

ALTER TABLE BBB_CORE_PRV.tbs_item DROP CONSTRAINT tbs_item_f2;

alter table BBB_CORE_PRV.tbs_ship_info drop CONSTRAINT tbs_shipitem_f;

alter table BBB_CORE_PRV.tbs_ship_info drop column SHIPPING_GROUP_ID;