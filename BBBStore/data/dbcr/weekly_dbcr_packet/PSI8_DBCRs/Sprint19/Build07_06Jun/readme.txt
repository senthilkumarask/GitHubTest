Execution Instructions:
=======================

1. Login to the database using sys schema.
2. Follow the below sequence to execute the DBCR for release.

	a)  Execute following DDL scripts in the given order.
		
		DDL/DDL_CORE_PRV.sql		For Core Prv Schema
		DDL/DDL_CORE.sql			For Core Schema	 
		
=========================
Rollback :
1. 	Follow the below sequence in order to Rollback the DDL for release.

		Rollback/Rollback_DDL_core_prv.sql				For Core Prv Schema
		Rollback/RollBack_DDL_core.sql					For Core Schema
