-- Build Version 18.5 PSI8 GEMINI RELEASE
SET ECHO ON;
SET DEFINE OFF;

update ecomadmin.reg_headers r set r.IS_PUBLIC = 'Y', r.LAST_MAINT_USER = 'Appadmin' where r.IS_PUBLIC is null;
update ecomadmin.arch_reg_headers r set r.IS_PUBLIC = 'Y', r.LAST_MAINT_USER = 'Appadmin' where r.IS_PUBLIC is null;


commit;