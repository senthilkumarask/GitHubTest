SET ECHO ON;
SET DEFINE OFF;

alter table BBB_PUB.BBB_SITE_ATTRIBUTES add (
  item_override_threshold number(10,2) null, 
  surcharge_override_threshold number(10,2) null
);
