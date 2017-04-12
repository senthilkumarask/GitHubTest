-- Build Version : PSI8 Release Sprint 0 to 12.1
SET ECHO ON;
SET DEFINE OFF;

INSERT INTO BBB_CORE_PRV.BBB_CLIENT_INFO (CLIENT_ID,CLIENT_TOKEN,TOKEN_EXPIRATION_DATE) VALUES ('eximuser','eximuser',null);
INSERT INTO BBB_CORE_PRV.BBB_CLIENT_ROLE (ID,SEQ_NUMBER,ROLE_ID) VALUES ('eximuser','0','10003');
INSERT INTO BBB_CORE_PRV.BBB_ROLE_PERSONA (ID,ROLE_NAME,PERSONA,IS_SECURE_PERSONA,APPLICABLE_SITE_ID) VALUES ('10003','eximuser','Profile$role$eximuser','Y',null);
INSERT INTO BBB_CORE_PRV.DPS_ROLE (ROLE_ID,TYPE,VERSION,NAME,DESCRIPTION) VALUES ('eximuser',2000,4,'eximuser','Exim User');

--Sprint 6.1

UPDATE BBB_CORE_PRV.BBB_SKU_ATTRIBUTES_INFO SET ACTION_URL = '/store/static/ltlDeliveryInfo' WHERE SKU_ATTRIBUTE_ID = '12_4';

--Sprint 7.1
UPDATE BBB_CORE_PRV.BBB_EXIM_CUSTOMIZATION_CODES SET DESCRIPTION = 'Etching' WHERE CUSTOMIZATION_CODE = 'ET';

UPDATE BBB_CORE_PRV.BBB_EXIM_CUSTOMIZATION_CODES SET DESCRIPTION = 'Monogramming' WHERE CUSTOMIZATION_CODE = 'MO';

UPDATE BBB_CORE_PRV.BBB_EXIM_CUSTOMIZATION_CODES SET DESCRIPTION = 'Printing' WHERE CUSTOMIZATION_CODE = 'SU';

UPDATE BBB_CORE_PRV.BBB_EXIM_CUSTOMIZATION_CODES SET DESCRIPTION = 'Etching-Printing' WHERE CUSTOMIZATION_CODE = 'ET-SU';

UPDATE BBB_CORE_PRV.BBB_EXIM_CUSTOMIZATION_CODES SET DESCRIPTION = 'Monogramming-Printing' WHERE CUSTOMIZATION_CODE = 'MO-SU';


--Sprint 11.1

UPDATE BBB_CORE_PRV.BBB_CUSTOMIZATION_VENDORS 
set API_VERSION='1',
API_URL='https://apitest.bbby.katori.com/api/customizations',
BAB_CLIENT_ID='54b74255ee31cb036d92d61cb5e6fa48246d889136b8b19b',API_KEY='S2ZlMx0XBAd2qiLh7ADukVWdjqLdNpqk',
BBB_CLIENT_ID='4146a237e41426d4c98330813db16e42804f68cf8f51fb4e',CAN_CLIENT_ID='7e4f86677d070df2d7d5813e79cba3dde1743ed05877fb8e',
TBS_BBB_CLIENT_ID='4146a237e41426d4c98330813db16e42804f68cf8f51fb4e',TBS_BAB_CLIENT_ID='54b74255ee31cb036d92d61cb5e6fa48246d889136b8b19b',
TBS_CAN_CLIENT_ID='7e4f86677d070df2d7d5813e79cba3dde1743ed05877fb8e' where CUSTOMIZATION_VENDOR_NAME='Katori';

--Sprint 12.1

INSERT INTO BBB_CORE_PRV.DPS_ROLE (ROLE_ID,TYPE,VERSION,NAME,DESCRIPTION) VALUES ('smart_inv_id',2000,4,'smart_inv_id','Smart User');
INSERT INTO BBB_CORE_PRV.BBB_ROLE_PERSONA (ID,ROLE_NAME,PERSONA,IS_SECURE_PERSONA,APPLICABLE_SITE_ID) VALUES ('10004','smart_inv_id','Profile$role$smart_inv_id','Y',null);
INSERT INTO BBB_CORE_PRV.BBB_CLIENT_INFO (CLIENT_ID,CLIENT_TOKEN,TOKEN_EXPIRATION_DATE) VALUES ('smart_inv_id','12xyzwinv',null);
INSERT INTO BBB_CORE_PRV.BBB_CLIENT_ROLE (ID,SEQ_NUMBER,ROLE_ID) VALUES ('smart_inv_id','0','10004');
UPDATE BBB_CORE_PRV.DPS_ROLE SET ROLE_ID='exim_inv_id',NAME='exim_inv_id' WHERE ROLE_ID='eximuser';
UPDATE BBB_CORE_PRV.BBB_ROLE_PERSONA SET PERSONA='Profile$role$exim_inv_id',ROLE_NAME='exim_inv_id' WHERE ID='10003';
UPDATE BBB_CORE_PRV.BBB_CLIENT_INFO SET CLIENT_ID='exim_inv_id',CLIENT_TOKEN='21xyzwinvexim' WHERE CLIENT_ID='eximuser';
UPDATE BBB_CORE_PRV.BBB_CLIENT_ROLE SET ID='exim_inv_id' WHERE ID='eximuser' AND SEQ_NUMBER='0';


UPDATE BBB_CORE_PRV.DAS_SEO_TAG SET DISPLAY_NAME='Easy Return Page',TITLE='Easy Returns',DESCRIPTION='Easy Returns' WHERE CONTENT_KEY = 'easyReturns';
UPDATE BBB_CORE_PRV.DAS_SEO_TAG SET TITLE='Safety and Recalls',DESCRIPTION='We are committed to providing customers with safe, high-quality products. Visit our Safety and Recalls page for information about product testing, lead content, and importing from China. Recall Information is also posted on our site.' WHERE CONTENT_KEY = 'safetyRecalls';
UPDATE BBB_CORE_PRV.DAS_SEO_TAG SET DISPLAY_NAME='Gift Card Policy',TITLE='Gift Card Policy',DESCRIPTION='Give the gift of shopping to a loved one with a Bed Bath & Beyond gift card- Check out bedbathandbeyond.com for everything you need to know about our gift card policy from shipping to balances to refunds- Learn more' WHERE CONTENT_KEY = 'giftCards';
UPDATE BBB_CORE_PRV.DAS_SEO_TAG SET TITLE='Shipping ',DESCRIPTION='We currently offer 3 shipping options within the United States: Standard, Expedited and Express. Visit our Shipping and Policies page for all of the details including information about costs, eligibility, tracking your package, gift packaging and more.' WHERE CONTENT_KEY = 'shippingInfo';

-- We are taking  backup of this table before removing data.So no rollback in rollback script
DELETE FROM BBB_CORE_PRV.DAS_SEO_TAG A WHERE EXISTS (SELECT * FROM BBB_CORE_PRV.BBB_PRODUCT B WHERE B.PRODUCT_ID=A.CONTENT_KEY);

--Sprint 17.2

INSERT INTO BBB_CORE_PRV.DPS_ROLE(ROLE_ID,TYPE,VERSION,NAME,DESCRIPTION) VALUES ('appuser',2000,4,'appuser','ClientId and Token for Mobile App user');
INSERT INTO BBB_CORE_PRV.BBB_ROLE_PERSONA(ID,ROLE_NAME,PERSONA,IS_SECURE_PERSONA,APPLICABLE_SITE_ID) VALUES ('10005','appuser','Profile$role$appuser','Y',null);
INSERT INTO BBB_CORE_PRV.BBB_CLIENT_INFO(CLIENT_ID,CLIENT_TOKEN,TOKEN_EXPIRATION_DATE) VALUES ('appuser','34b50385-85e8-4815-914c-970262fc5bbc',null);
INSERT INTO BBB_CORE_PRV.BBB_CLIENT_ROLE(ID,SEQ_NUMBER,ROLE_ID) VALUES ('appuser','0','10005');

INSERT INTO BBB_CORE_PRV.BBB_REPO_INVALIDATE_SCHEDULE (ID,PROCESS_NAME,LAST_MODIFIED_DATE,READY_TO_INVALIDATE) VALUES ('1','InvalidateLocalStoreRepositoryCacheScheduler',TO_TIMESTAMP('05-May-16 10.12.15.000000000 PM','DD-MON-RR HH.MI.SS.FF AM'),0);

Insert INTO BBB_CORE_PRV.BBB_DUMMY_STORES values ('1','BuyBuyBaby','3328','328');
Insert INTO BBB_CORE_PRV.BBB_DUMMY_STORES values ('2','BuyBuyBaby','3331','1331');
Insert INTO BBB_CORE_PRV.BBB_DUMMY_STORES values ('3','BuyBuyBaby','3355','355');
Insert INTO BBB_CORE_PRV.BBB_DUMMY_STORES values ('4','BuyBuyBaby','3361','361');
Insert INTO BBB_CORE_PRV.BBB_DUMMY_STORES values ('5','BuyBuyBaby','3920','20');
Insert INTO BBB_CORE_PRV.BBB_DUMMY_STORES values ('6','BuyBuyBaby','3936','36');
Insert INTO BBB_CORE_PRV.BBB_DUMMY_STORES values ('7','BuyBuyBaby','3947','547');
Insert INTO BBB_CORE_PRV.BBB_DUMMY_STORES values ('8','BuyBuyBaby','3949','1149');
Insert INTO BBB_CORE_PRV.BBB_DUMMY_STORES values ('9','BuyBuyBaby','3967','167');
Insert INTO BBB_CORE_PRV.BBB_DUMMY_STORES values ('10','BuyBuyBaby','3994','194');
COMMIT;



