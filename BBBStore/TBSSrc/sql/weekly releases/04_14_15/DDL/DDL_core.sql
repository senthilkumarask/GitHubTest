SET ECHO ON;
SET DEFINE OFF;

ALTER TABLE BBB_CORE.tbs_ship_info ADD (
	tax_exempt_id varchar2(40) null
);

