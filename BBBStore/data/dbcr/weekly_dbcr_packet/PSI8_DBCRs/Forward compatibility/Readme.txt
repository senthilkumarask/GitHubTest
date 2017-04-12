-- Gemini Merge Consolidated Packet Sprint 0 to sprint 18.2

Execution Instructions:
=======================

1. Login to the database using sys schema.
2. Follow the below sequence to execute the DBCR for release.
	
	a)  Execute following DML scripts in the given order.
	
		
		When DML is to be applied on Staging:		/DML/DML_bbb_core_prv.sql				For core_prv Schema
		
		When DML is to be applied on Prouction:		/DML/DML_bbb_switch_A.sql				For switch a Schema
													/DML/DML_bbb_switch_B.sql				For switch b Schema

	
