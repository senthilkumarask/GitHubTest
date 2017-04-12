SET ECHO ON;
SET DEFINE OFF;

alter table BBB_CORE_PRV.BBB_SITE_ATTRIBUTES add (
  shipping_override_threshold number(10,2) null
);
