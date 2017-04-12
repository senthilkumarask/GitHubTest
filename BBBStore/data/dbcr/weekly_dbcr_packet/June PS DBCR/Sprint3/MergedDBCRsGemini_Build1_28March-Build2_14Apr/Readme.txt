Execution Instructions:
=======================

1. Login to the database using sys schema.
2. Follow the below sequence to execute the DBCR for release.
	
			
	a)  Execute following DDL in the given order.
	             
				/DML/DML_core.sql				For core Schema
				/DML/DML_core_prv.sql			For core_prv Schema
				
	
Rollback Steps:
===============
	a)  Execute following DDL and DML scripts in the given order.
	
	
				/Rollback/DML/Rollback_DML_core.sql				 For core Schema
				/Rollback/DML/Rollback_DML_core_prv.sql			 For core_prv Schema
				
3. Follow below sequence for Stored procedure:

	Stored Procedure/GET_IN_COUNTRY.fnc
	Stored Procedure/AtgWs_Package.sql
	Stored Procedure/AtgWs_Package_Body.sq
	Stored Procedure/core_prv/ATG_ENDECAEXPORT_PKG_CORE_PRV
	Stored Procedure/core_prv/ATG_ENDECAEXPORT_PKG_BODY_CORE_PRV
	Stored Procedure/core_prv/ATG_ENDECA_PARTIAL_PKG_BODY_CORE_PRV

4. Execute following Grant.sql scripts:

		Grant.sql

