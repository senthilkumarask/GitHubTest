-- BUILD VERSION : PSI8 RELEASE SPRINT17 BUILD2
SET ECHO ON;
SET DEFINE OFF;

DELETE FROM BBB_CORE_PRV.BBB_CLIENT_INFO WHERE CLIENT_ID='appuser';
DELETE FROM BBB_CORE_PRV.BBB_CLIENT_ROLE WHERE ID='appuser' AND SEQ_NUMBER=0;
DELETE FROM BBB_CORE_PRV.BBB_ROLE_PERSONA WHERE ID='10005';
DELETE FROM BBB_CORE_PRV.DPS_ROLE WHERE ROLE_ID='appuser';


COMMIT;
