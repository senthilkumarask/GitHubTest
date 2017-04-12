1) 	Please note we need to execute the script placed under CORE folder, prior to any other scripts placed under other folders.
2) 	Following folders have Endeca SPs corresponding to that schema. Please execute accordingly.
		CORE_PRV	Staging
		SWITCH_A	Prod
		SWITCH_B	Prod
		EStage		Staging & Switch A/B
3)	DDL Scripts is the folder containing Schema wise DDL's.
		DDL_Master_CMN			For PCI Schema change related DDL's
		DDL_Master_PIM			For PIM STG related changes (Not supposed to be done in Prod/ UAT OR any other env where PIM changes are done by PIM Team.
		DDL_Master_Core			For Core Schema
		DDL_Master_Core_Prv		For Staging Schema
		DDL_Master_Pub			For Pub Schema
		DDL_Master_Switch_A		For Switch A Schema
		DDL_Master_Switch_B		For Switch B Schema
4)	DML Scripts is the folder containing Schema wise DML's.
		DML_Master_Core			For Core Schema
		DML_Master_Core_Prv		For Staging Schema
		DML_Master_Pub			For Pub Schema
		DML_Master_Switch_A		For Switch A Schema
		DML_Master_Switch_B		For Switch B Schema
		
