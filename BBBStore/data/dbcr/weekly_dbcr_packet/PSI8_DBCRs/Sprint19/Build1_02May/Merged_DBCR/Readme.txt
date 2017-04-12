To run Stored Procedure:
=================
Execution Instructions:
=======================

1. Login to the database using sys schema.
	Execute following Grant.sql scripts:

		Grant.sql
2. Directly Compile the Stored Procedures in the below Order

	/STORED_PROCEDURE/core_prv/ATG_ENDECAEXPORT_PKG_CORE_PRV.sql			For core_prv Schema;
	/STORED_PROCEDURE/core_prv/ATG_ENDECAEXPORT_PKG_BODY_CORE_PRV.sql		For core_prv Schema; 
	/STORED_PROCEDURE/core_prv/ATG_ENDECA_PARTIAL_PKG_BODY_CORE_PRV.sql		For core_prv Schema; 
	
	/STORED_PROCEDURE/switch_a/ATG_ENDECAEXPORT_PKG_SWITCH_A.sql		    For Switch A  Schema		
	/STORED_PROCEDURE/switch_a/ATG_ENDECAEXPORT_PKG_BODY_SWITCH_A.sql
	/STORED_PROCEDURE/switch_a/ATG_ENDECA_PARTIAL_PKG_BODY_SWITCH_A.sql
	
	
	
	/STORED_PROCEDURE/switch_b/ATG_ENDECAEXPORT_PKG_SWITCH_B.sql			for Switch B Schema
	/STORED_PROCEDURE/switch_b/ATG_ENDECAEXPORT_PKG_BODY_SWITCH_B.sql	
	/STORED_PROCEDURE/switch_b/ATG_ENDECA_PARTIAL_PKG_BODY_SWITCH_B.sql
	
