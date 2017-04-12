-- Gemini Sprint 18 build 5

Execution Instructions:
=======================

1. Login to the database using sys schema.
2. Follow the below sequence to execute the DBCR for release.
		
	a)  Execute following DDL and DML scripts in the given order.
	
				/DDL/DDL_core.sql				For core Schema
				/DDL/DDL_core_prv.sql			For core_prv Schema
				/DML/DML_EcomAdmin.sql			For Ecom Admin sche           Note : This is for lower environments only
      
	 b) Execute the stored procedure in following order.
		Stored Procedure/Core/OMNITURE_REPORT_DATA_PKG_Core.sql         		For core schema	
		Stored Procedure/Core/OMNITURE_REPORT_DATA_PKG Body_Core.sql			For core schema	
		
		Stored Procedure/Core_Prv/OMNITURE_REPORT_DATA_PKG_Core_Prv.sql			For core prv  schema	
		Stored Procedure/Core_Prv/OMNITURE_REPORT_DATA_PKG Body_Core_Prv.sql	For core prv  schema
		
		Stored Procedure/ecomadmin/AtgWs_Package3_566.sql						For Ecom Admin schema
		Stored Procedure/ecomadmin/AtgWs_Package_Body3_566.sql					For Ecom Admin schema
		Stored Procedure/ecomadmin/GET_IN_COUNTRY_3.fnc							For Ecom Admin schema


	
Rollback Steps:
===============
	-  Execute following DDL and DML scripts in the given order.	
	
				/Rollback/DDL/RollBack_DDL_core.sql 	     	For core Schema
              	/Rollback/DDL/RollBack_DDL_core_prv.sql 	     For core_prv Schema
				
