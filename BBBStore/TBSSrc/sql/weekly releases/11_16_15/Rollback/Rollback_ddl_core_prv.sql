SET ECHO ON;
SET DEFINE OFF;

alter table BBB_CORE_PRV.bbb_order drop column auto_waive_classification;
alter table BBB_CORE_PRV.bbb_order drop column auto_waive_flag;
