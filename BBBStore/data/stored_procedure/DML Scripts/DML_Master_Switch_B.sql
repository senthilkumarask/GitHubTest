SET DEFINE OFF;
UPDATE BBB_SWITCH_B.BBB_STATES SET IS_MILITARY_STATE=1 WHERE STATE_ID IN('AA','AE','AP');
COMMIT;