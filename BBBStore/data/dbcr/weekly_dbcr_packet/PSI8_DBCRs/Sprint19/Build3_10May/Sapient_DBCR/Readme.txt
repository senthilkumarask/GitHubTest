-- Gemini Sprint 19 build 3

Execution Instructions:
=======================

1. Login to the database using sys schema.
2. Follow the below sequence to execute the DBCR for release.


	 a) Execute the stored procedure in following order.
		Stored Procedure/Core/OMNITURE_REPORT_DATA_PKG_Core.sql         		For core schema	
		Stored Procedure/Core/OMNITURE_REPORT_DATA_PKG Body_Core.sql			For core schema	
		
		Stored Procedure/Core_Prv/OMNITURE_REPORT_DATA_PKG_Core_Prv.sql			For core prv  schema	
		Stored Procedure/Core_Prv/OMNITURE_REPORT_DATA_PKG Body_Core_Prv.sql	For core prv  schema	
		Stored Procedure/Core_Prv/ATG_ENDECAEXPORT_PKG_CORE_PRV.sql				For core_prv Schema;
		Stored Procedure/Core_Prv/ATG_ENDECAEXPORT_PKG_BODY_CORE_PRV.sql		For core_prv Schema;
		Stored Procedure/Core_Prv/ATG_ENDECA_PARTIAL_PKG_BODY_CORE_PRV.sql		For core_prv Schema;
	
		Stored Procedure/switch_a/ATG_ENDECA_PARTIAL_PKG_BODY_SWITCH_A.sql		For Switch A  Schema
	
	
	
		Stored Procedure/switch_b/ATG_ENDECA_PARTIAL_PKG_BODY_SWITCH_B.sql		For Switch B Schema
	


				
