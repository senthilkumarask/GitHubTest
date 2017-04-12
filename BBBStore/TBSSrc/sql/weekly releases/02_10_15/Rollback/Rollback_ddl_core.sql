SET ECHO ON;
SET DEFINE OFF;

alter table BBB_CORE.tbs_ship_info drop CONSTRAINT tbs_shipitem_f;

alter table BBB_CORE.tbs_ship_info drop column SHIPPING_GROUP_ID;