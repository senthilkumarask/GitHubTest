-- build version: 2.04.01.001
SET ECHO ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;

--------------------------------------------------------
--  DDL for Package UPDATE_RECORDS_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BBB_CORE_PRV.UPDATE_RECORDS_PKG IS
    PROCEDURE update_records_bbb_core_prv;     
END UPDATE_RECORDS_PKG; 

/
SHOW ERROR;
COMMIT;