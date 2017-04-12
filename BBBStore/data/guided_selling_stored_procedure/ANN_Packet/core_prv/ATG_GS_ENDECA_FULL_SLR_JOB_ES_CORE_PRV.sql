SET DEFINE OFF;
SET SERVEROUTPUT ON;
BEGIN
    SYS.DBMS_SCHEDULER.CREATE_JOB (
            job_name => 'BBB_CORE_PRV.BBB_GS_ENDECA_FULL_SLR_JOB_ES',
            job_type => 'PLSQL_BLOCK',
            job_action => 'BEGIN BBB_CORE_PRV.ATG_GS_ENDECAEXPORT_PARTIALPKG.Export_GS_Full_Controller (''production'',''East''); END;',
            number_of_arguments => 0,
            job_class => 'DEFAULT_JOB_CLASS',
            enabled => true,
            auto_drop => false,
            comments => 'Job defined entirely by the CREATE JOB procedure.',
            credential_name => NULL,
            destination_name => NULL,
			repeat_interval => 'FREQ=DAILY;BYHOUR=0,2,4,6,8,10,12,14,16,18,20,22;BYMINUTE=0;BYSECOND=0');

END;
/
COMMIT;