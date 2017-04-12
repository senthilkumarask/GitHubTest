SET ECHO ON;
SET DEFINE OFF;

alter table BBB_CORE_PRV.tbs_ship_info drop column tax_exempt_id;
alter table BBB_CORE_PRV.BBB_SITE_ATTRIBUTES drop column tax_override_threshold;