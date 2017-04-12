BEGIN
    SYS.DBMS_SCHEDULER.CREATE_JOB (
            job_name => '"STOFU_QA_BK_SWITCH_A"."BBB_GS_ENDECA_PAR_SCHDLR_JOB"',
            schedule_name => '"SYS"."BSLN_MAINTAIN_STATS_SCHED"',
            job_type => 'PLSQL_BLOCK',
            job_action => 'BEGIN ATG_GS_EndecaExport_PartialPkg.Export_GS_Partial_Controller (''production'',''East''); END;',
            number_of_arguments => 0,
            job_class => 'DEFAULT_JOB_CLASS',
            enabled => true,
            auto_drop => false,
            comments => 'Job defined entirely by the CREATE JOB procedure.',
            credential_name => NULL,
            destination_name => NULL);
END;
