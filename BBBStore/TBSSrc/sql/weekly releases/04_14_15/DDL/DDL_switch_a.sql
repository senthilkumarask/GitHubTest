SET ECHO ON;
SET DEFINE OFF;

alter table BBB_SWITCH_A.BBB_SITE_ATTRIBUTES add (
  tax_override_threshold number(10,2) null
);
