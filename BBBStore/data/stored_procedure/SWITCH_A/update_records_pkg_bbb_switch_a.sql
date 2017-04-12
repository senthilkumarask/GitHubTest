
SET ECHO ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;

--------------------------------------------------------
--  DDL for Package UPDATE_RECORDS_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BBB_SWITCH_A.UPDATE_RECORDS_PKG IS
    PROCEDURE update_records_bbb_switch_a;     
END UPDATE_RECORDS_PKG; 

/
SHOW ERROR;
COMMIT;