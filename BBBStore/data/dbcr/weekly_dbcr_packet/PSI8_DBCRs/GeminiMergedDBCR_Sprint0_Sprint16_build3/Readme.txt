-- Gemini Merge Consolidated Packet Sprint 0 to sprint 17.1

Execution Instructions:
=======================

1. Login to the database using sys schema.
2. Follow the below sequence to execute the DBCR for release.
	a)  Execute following Grant.sql scripts:

		Grant.sql

	b) Please take back up of "DAS_SEO_TAG" table on below schema .
	    BBB_CORE_PRV
		BBB_SWITCH_A
		BBB_SWITCH_B
		BBB_PUB
			
	c)  Execute following DDL and DML scripts in the given order.
	
		Please Note:- /DML/DML_pub.sql    This will take longer time to run  the query (DELETE FROM BBB_PUB.DAS_SEO_TAG A WHERE EXISTS (SELECT * FROM BBB_PUB.BBB_PRODUCT B WHERE B.PRODUCT_ID=A.CONTENT_KEY);)
		
				/DDL/DDL_core.sql				For core Schema
				/DDL/DDL_core_prv.sql			For core_prv Schema
                /DDL/DDL_Switch_A.sql           For switch A Schema
                /DDL/DDL_Switch_B.sql           For switch b schema
                /DDL/DDL_pub.sql                For pub schema
				/DDL/DDL_cmn.sql           		For CMN  schema
				/DDL/DDL_ecom_admin				For ecom admin
				/DDL/DDL_pim					FOr PIM
				
				/DML/DML_core.sql				For core Schema
				/DML/DML_pub.sql                For pub schema
				/DML/DML_core_prv.sql			For core_prv Schema
                /DML/DML_switch_a.sql           For switch A Schema
                /DML/DML_switch_b.sql           For switch b schema
				


	
Rollback Steps:
===============
	a)  Execute following DDL and DML scripts in the given order.	
	
				/Rollback/DDL/RollBack_DDL_core.sql 	     	For core Schema
              	/Rollback/DDL/RollBack_DDL_core_prv.sql 	     For core_prv Schema
                /Rollback/DDL/RollBack_DDL_switch_a.sql          For switch A Schema
                /Rollback/DDL/RollBack_DDL_switch_b.sql          For switch b schema
                /Rollback/DDL/RollBack_DDL_pub.sql               For pub schema  
				/Rollback/DDL/RollBack_DDL_cmn.sql         		 For CMN  Schema
			    /Rollback/DDL/Rollback_pim.sql         		     For PIM  Schema
				/Rollback/DDL/Rollback_ecom_admin.sql         	 For ECOM ADMIN Schema

				/Rollback/DML/Rollback_DML_core.sql				For core Schema
				/Rollback/DML/RollBack_DML_pub.sql 	    		For pub schema
                /Rollback/DML/RollBack_DML_core_prv.sql         For core_prv Schema
				/Rollback/DML/RollBack_DML_switch_a.sql         For switch A schema
				/Rollback/DML/RollBack_DML_switch_b.sql         For switch B schema
				
To run Stored Procedure:
=================
Execution Instructions:
=======================

1. Login to the database using sys schema.
2. Directly Compile the Stored Procedures in the below Order

	
		            		
	/STORED_PROCEDURE/ecomadmin/GET_IN_COUNTRY.fnc							for ECOMADMIN Schema
	/STORED_PROCEDURE/ecomadmin/ATGWS_PACKAGE.sql
	/STORED_PROCEDURE/ecomadmin/ATGWS_PACKAGE_BODY.sql
	
	/STORED_PROCEDURE/core/OMNITURE_REPORT_DATA_PKG_CORE.sql				For core Schema
	/STORED_PROCEDURE/core/OMNITURE_REPORT_DATA_PKG_BODY_CORE.sql			For core Schema
	
	/STORED_PROCEDURE/core_prv/ATG_ENDECAEXPORT_PKG_BODY_CORE_PRV.sql		For core_prv Schema
	/STORED_PROCEDURE/core_prv/OMNITURE_REPORT_DATA_PKG_CORE_PRV.sql		For core_prv Schema
	/STORED_PROCEDURE/core_prv/OMNITURE_REPORT_DATA_PKG_BODY_CORE_PRV.sql	For core_prv Schema
	
	/STORED_PROCEDURE/switch_a/ATG_ENDECAEXPORT_PKG_SWITCH_A.sql		     for Switch A  Schema		
	/STORED_PROCEDURE/switch_a/ATG_ENDECAEXPORT_PKG_BODY_SWITCH_A.sql
	/STORED_PROCEDURE/switch_a/ATG_ENDECA_PARTIAL_PKG_BODY_SWITCH_A.sql
	
	
	
	/STORED_PROCEDURE/switch_b/ATG_ENDECAEXPORT_PKG_SWITCH_B.sql			for Switch B Schema
	/STORED_PROCEDURE/switch_b/ATG_ENDECAEXPORT_PKG_BODY_SWITCH_B.sql	
	/STORED_PROCEDURE/switch_b/ATG_ENDECA_PARTIAL_PKG_BODY_SWITCH_B.sql
	
	
	
Note: Please follow readme file of "ATG Upgrade Scripts only for Test1 & Test5"  for upgrade . 
	
