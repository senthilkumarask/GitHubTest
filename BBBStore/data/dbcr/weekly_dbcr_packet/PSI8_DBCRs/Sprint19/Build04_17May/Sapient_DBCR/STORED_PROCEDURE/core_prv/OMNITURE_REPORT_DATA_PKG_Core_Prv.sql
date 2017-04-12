-- Build Version : PSI8 Release Sprint19 Build4
SET ECHO ON;
set DEFINE OFF;
set SERVEROUTPUT ON;

--------------------------------------------------------
--  File created - Tuesday-May-17-2016  
--  DDL for Package OMNITURE_REPORT_DATA_PKG
--------------------------------------------------------
CREATE OR REPLACE
PACKAGE BBB_CORE_PRV.OMNITURE_REPORT_DATA_PKG IS

    PROCEDURE ARCHIVE_OMNITURE_DATA (p_report_id in VARCHAR, p_concept IN VARCHAR, p_records_moved OUT NUMBER);
    PROCEDURE PURGE_OMNITURE_DATA (p_report_id in VARCHAR, p_concept IN VARCHAR, p_records_moved OUT NUMBER);
	
END OMNITURE_REPORT_DATA_PKG;
/
SHOW ERROR;
COMMIT;