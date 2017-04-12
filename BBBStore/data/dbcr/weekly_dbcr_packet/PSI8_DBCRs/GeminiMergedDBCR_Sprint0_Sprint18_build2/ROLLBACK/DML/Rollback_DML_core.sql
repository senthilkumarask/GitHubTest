-- Build Version : PSI8 Release Sprint 0 to 12.1
SET ECHO ON;
SET DEFINE OFF;



--Sprint 7.1
UPDATE BBB_CORE.BBB_EXIM_CUSTOMIZATION_CODES SET DESCRIPTION = 'Personalization';

--Sprint 12.1
DELETE FROM BBB_CORE.BBB_ROLE_PERSONA WHERE ID='10004';
DELETE FROM BBB_CORE.BBB_CLIENT_ROLE WHERE ID='smart_inv_id' AND  SEQ_NUMBER='0';
DELETE FROM BBB_CORE.BBB_CLIENT_INFO WHERE CLIENT_ID='smart_inv_id';
DELETE FROM BBB_CORE.DPS_ROLE WHERE ROLE_ID='smart_inv_id';
UPDATE BBB_CORE.DPS_ROLE SET ROLE_ID='eximuser',NAME='eximuser' WHERE ROLE_ID='exim_inv_id';
UPDATE BBB_CORE.BBB_ROLE_PERSONA SET PERSONA='Profile$role$eximuser',ROLE_NAME='eximuser' WHERE ID='10003';
UPDATE BBB_CORE.BBB_CLIENT_INFO SET CLIENT_ID='eximuser',CLIENT_TOKEN='eximuser' WHERE CLIENT_ID='exim_inv_id';
UPDATE BBB_CORE.BBB_CLIENT_ROLE SET ID='eximuser' WHERE ID='exim_inv_id' AND SEQ_NUMBER='0';

DELETE FROM BBB_CORE.BBB_CLIENT_INFO where CLIENT_ID='eximuser';
DELETE FROM BBB_CORE.BBB_CLIENT_ROLE where ID='eximuser' AND SEQ_NUMBER=0;
DELETE FROM BBB_CORE.BBB_ROLE_PERSONA where ID='10003';
DELETE FROM BBB_CORE.DPS_ROLE where ROLE_ID='eximuser';

--Sprint 17.2

DELETE FROM BBB_CORE.BBB_CLIENT_INFO WHERE CLIENT_ID='appuser';
DELETE FROM BBB_CORE.BBB_CLIENT_ROLE WHERE ID='appuser' AND SEQ_NUMBER=0;
DELETE FROM BBB_CORE.BBB_ROLE_PERSONA WHERE ID='10005';
DELETE FROM BBB_CORE.DPS_ROLE WHERE ROLE_ID='appuser';


commit;