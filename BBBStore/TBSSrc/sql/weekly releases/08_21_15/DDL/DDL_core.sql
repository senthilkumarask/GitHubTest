SET ECHO ON;
SET DEFINE OFF;

ALTER TABLE BBB_CORE.bbb_item MODIFY shipTime varchar2(250);

commit;