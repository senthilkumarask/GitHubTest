-- build version: 2.04.01.001
SET ECHO ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;

--------------------------------------------------------
--  File created - Tuesday-January-15-2013   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package ATG_ENDECAEXPORT_PARTIAL_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BBB_CORE_PRV.ATG_ENDECAEXPORT_PARTIAL_PKG IS
    
    PROCEDURE Export_Controller (p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Full_Controller (p_Id varchar2, p_dataCenter varchar2, p_feedType varchar2);
     
END ATG_EndecaExport_Partial_Pkg; 

/
SHOW ERROR;
COMMIT;
