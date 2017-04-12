-- Build Version : PSI8 Release Sprint 0 to 12.1
-- sprint 11.2
SET ECHO ON;
SET DEFINE OFF;


ALTER TABLE BBB_PIM_STG.ECP_ITEMS_RARE ADD (
   WEB_SKU_STATUS_CD char (1 char) NULL,
   CA_CHAIN_SKU_STATUS_CD char (1 char) NULL,
   CA_WEB_SKU_STATUS_CD char (1 char) NULL,
   CHAIN_SKU_STATUS_CD char (1 char) NULL
);

COMMIT;