SET ECHO ON;
SET DEFINE OFF;

ALTER TABLE BBB_CORE_PRV.bbb_item MODIFY shipTime varchar2(250);

commit;