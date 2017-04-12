Execution Instructions:
=======================
TBS Drop 2

1. Login to the database using sys schema.
2. Follow the below sequence in order to execute the DBCR for release.

	DDL\DDL_switch_a.sql
	DDL\DDL_switch_b.sql
	DDL\DDL_core_prv.sql
	DDL\DDL_pub.sql


RollBack Steps:
===============

1.Rollback\Rollback_ddl_switch_a.sql
2.Rollback\Rollback_ddl_switch_b.sql
3.Rollback\Rollback_ddl_core_prv.sql
4.Rollback\Rollback_ddl_pub.sql
