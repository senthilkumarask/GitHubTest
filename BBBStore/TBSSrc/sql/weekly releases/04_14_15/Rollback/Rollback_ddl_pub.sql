SET ECHO ON;
SET DEFINE OFF;

alter table BBB_PUB.BBB_SITE_ATTRIBUTES drop column tax_override_threshold;

commit;