SET ECHO ON;
SET DEFINE OFF;

alter table BBB_CORE.bbb_order add auto_waive_classification varchar2(50) null;
alter table BBB_CORE.bbb_order add auto_waive_flag number(1) null;
