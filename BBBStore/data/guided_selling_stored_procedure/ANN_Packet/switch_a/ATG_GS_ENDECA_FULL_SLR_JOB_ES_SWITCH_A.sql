SET DEFINE OFF;
SET SERVEROUTPUT ON;
BEGIN
    SYS.DBMS_SCHEDULER.CREATE_JOB (
            job_name => 'BBB_SWITCH_A.BBB_GS_ENDECA_FULL_SLR_JOB_ES',
            job_type => 'PLSQL_BLOCK',
            job_action => 'BEGIN BBB_SWITCH_A.ATG_GS_ENDECAEXPORT_PARTIALPKG.Export_GS_Full_Controller (''production'',''East''); END;',
            number_of_arguments => 0,
            job_class => 'DEFAULT_JOB_CLASS',
            enabled => true,
            auto_drop => false,
            comments => 'Job defined entirely by the CREATE JOB procedure.',
            credential_name => NULL,
            destination_name => NULL,
			repeat_interval => 'FREQ=DAILY;BYHOUR=8;BYMINUTE=30;BYSECOND=0');

END;
/
COMMIT;