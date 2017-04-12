-- build version: Andromeda_AugPS_11Jul
SET ECHO ON;
SET SERVEROUTPUT ON;
SET DEFINE OFF;
BEGIN
	
  SYS.dbms_scheduler.create_job(
      job_name => 'BBB_CORE_PRV.DAILY_AMB_CATALOG_FEED',
      job_type => 'PLSQL_BLOCK',
      job_action => 'BEGIN BBB_CORE_PRV.SEND_DELTA_CATALOG_TO_AMB(''5004'', ''5005'', ''WEST''); END;',
      start_date => sysdate,
      repeat_interval => 'FREQ=DAILY; BYHOUR=0,8,12; BYMINUTE=30;',
      enabled => FALSE,
      comments => 'Runtime: Send delta catalog to BorderFree Every day 3 times after catalog updates in BCC.');
  SYS.DBMS_SCHEDULER.enable ('BBB_CORE_PRV.DAILY_AMB_CATALOG_FEED');
END;
/
SHOW ERROR;
COMMIT;