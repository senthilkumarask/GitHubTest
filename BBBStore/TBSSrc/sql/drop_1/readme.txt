Execution Instructions:
=======================
TBS Drop 1

1. Login to the database using sys schema.
2. Follow the below sequence in order to execute the DBCR for release.

	DDL\DDL_core.sql
	DDL\DDL_core_prv.sql
	DDL\DDL_pub.sql

3. Golden Gate information is contained in TBSNext_GoldenGate_TableInfo_17sep2014.xls


RollBack Steps:
===============

1.Rollback\Rollback_ddl_core.sql
2.Rollback\Rollback_ddl_core_prv.sql
3.Rollback\Rollback_ddl_pub.sql
