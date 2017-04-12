-- Build PSI8 SPRINT14 BUILD 1_08Feb 2.08.14.001

|---------------------------------------------------------------------------------------------|
|                                                                                             |
|                                                                                             |
|      This DBCR Only for test1 and test5 environment .do not run on any other environment    |
|                                                                                             |
|                                                                                             |
|---------------------------------------------------------------------------------------------|



Execution Instructions:
=======================
FOR DDLs : 

1. Login to the database using sys schema.
2. Follow the below sequence to execute the DBCR for release.

	a)  Execute following DDL scripts in the given order.
 
	/DDL/core_DDL.sql             For core schema	
	/DDL/core_prv_DDL.sql          For core prv  schema
	/DDL/pub_DDL.sql              For pub schema	
	/DDL/switch_a_DDL.sql         For switch a  schema	
	/DDL/switch_b_DDL.sql.sql     For switch b schema		

	b)  Execute following DML scripts in the given order.
	
	/DML/DML_core.sql			For core schema	 
	
Rollback Steps:
===============
	a)  Execute following DDL scripts in the given order.	

	/Rollback/DDL/Rollback_core_DDL.sql      For core schema		
	/Rollback/DDL/Rollback_core_prv_DDL.sql   For core prv schema
	/Rollback/DDL/Rollback_pub_DDL.sql       For pub schema		
	/Rollback/DDL/Rollback_switch_a_DDL.sql  For switch a  schema
	/Rollback/DDL/Rollback_switch_b_DDL.sql  For switch b  schema		
					
				
