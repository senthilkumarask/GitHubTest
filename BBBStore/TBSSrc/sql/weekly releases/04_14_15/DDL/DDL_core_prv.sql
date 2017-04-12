SET ECHO ON;
SET DEFINE OFF;

ALTER TABLE BBB_CORE_PRV.tbs_ship_info ADD (
	tax_exempt_id varchar2(40) null
);

alter table BBB_CORE_PRV.BBB_SITE_ATTRIBUTES add (
  tax_override_threshold number(10,2) null 
);