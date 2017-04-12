SET ECHO ON;
SET DEFINE OFF;

alter table BBB_SWITCH_A.BBB_SITE_ATTRIBUTES drop column tax_override_threshold;

commit;