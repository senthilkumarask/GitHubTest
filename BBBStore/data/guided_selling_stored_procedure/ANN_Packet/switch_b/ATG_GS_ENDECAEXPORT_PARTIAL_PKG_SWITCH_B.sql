SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE BBB_SWITCH_B.ATG_GS_ENDECAEXPORT_PARTIALPKG IS
    
    PROCEDURE Export_GS_Partial_Controller (p_Id varchar2, p_dataCenter varchar2);

    PROCEDURE Export_GS_Full_Controller (p_Id varchar2, p_dataCenter varchar2);
     
END ATG_GS_EndecaExport_PartialPkg;
/
show error;
COMMIT;