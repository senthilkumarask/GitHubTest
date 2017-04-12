Execution Instructions:
=======================
TBS Drop 4

1. Login to the database using sys schema.
2. Follow the below sequence in order to execute the DBCR for release.

	DDL\DDL_core.sql
	DDL\DDL_core_prv.sql


RollBack Steps:
===============

1. Login to the database using sys schema.
2. Follow the below sequence in order to execute the DBCR for release.

	Rollback\Rollback_ddl.sql
	Rollback\Rollback_ddl_core.sql

Dataload steps:
===============

1. Create a new project in the BCC and import all of the datafiles in the Dataload directory in the order they are named.
2. Deploy the project 
