
SET ECHO ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;

--------------------------------------------------------
--  DDL for Package UPDATE_RECORDS_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BBB_PUB.UPDATE_RECORDS_PKG IS
    PROCEDURE update_records_bbb_pub;     
END UPDATE_RECORDS_PKG; 

/
SHOW ERROR;
COMMIT;