-- BUILD VERSION : PSI8 RELEASE SPRINT14 BUILD1
SET ECHO ON;
SET DEFINE OFF;

--10.1 TO 10.2 
-- USER_DDL.SQL
CREATE UNIQUE INDEX BBB_SWITCH_A.DPS_USER_U1 ON BBB_SWITCH_A.DPS_USER (LOGIN,REALM_ID);
ALTER TABLE BBB_SWITCH_A.BBB_CATEGORY ADD RANK VARCHAR2(50);


COMMIT;