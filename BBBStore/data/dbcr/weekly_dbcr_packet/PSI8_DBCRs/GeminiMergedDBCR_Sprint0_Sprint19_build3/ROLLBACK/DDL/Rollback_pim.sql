-- Build Version : PSI8 Release Sprint 0 to 12.1
SET ECHO ON;
SET DEFINE OFF;
--Sprint 11.2

ALTER TABLE BBB_PIM_STG.ECP_ITEMS_RARE
DROP (WEB_SKU_STATUS_CD, CA_CHAIN_SKU_STATUS_CD, CA_WEB_SKU_STATUS_CD, CHAIN_SKU_STATUS_CD);

COMMIT;

