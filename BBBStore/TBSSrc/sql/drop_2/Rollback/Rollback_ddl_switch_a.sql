SET ECHO ON;
SET DEFINE OFF;

alter table BBB_SWITCH_A.BBB_SITE_ATTRIBUTES drop column item_override_threshold; 
alter table BBB_SWITCH_A.BBB_SITE_ATTRIBUTES drop column surcharge_override_threshold;

commit;