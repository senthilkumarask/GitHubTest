SET ECHO ON;
SET DEFINE OFF;

alter table BBB_SWITCH_B.BBB_SITE_ATTRIBUTES add (
  shipping_override_threshold number(10,2) null
);
