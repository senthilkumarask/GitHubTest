-- Build Version :

SET ECHO ON;
SET DEFINE OFF;

---- update config translations : BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS

UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='l5v4y' WHERE TRANSLATION_ID='BBBPSI6CKT10004';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='l5v4y' WHERE TRANSLATION_ID='BBBPSI6CKT10005';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='6031851168480' WHERE TRANSLATION_ID='BBBPSI6CKT10007';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='2.08.19.004' WHERE TRANSLATION_ID='PUB1100019';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='2.08.19.004' WHERE TRANSLATION_ID='PUB1100018';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='2.08.19.004' WHERE TRANSLATION_ID='PUB1100017';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='300000' WHERE TRANSLATION_ID='500001_COHERENCE_ENABLE_THRESHOLD_TIME_BBCM';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='300000' WHERE TRANSLATION_ID='500001_COHERENCE_ENABLE_THRESHOLD_TIME_BBBM';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='300000' WHERE TRANSLATION_ID='500001_COHERENCE_ENABLE_THRESHOLD_TIME_BBUM';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='true' WHERE TRANSLATION_ID='800002_enableLTLRegForSite_BBUM';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='true' WHERE TRANSLATION_ID='800002_enableLTLRegForSite_BBBM';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='true' WHERE TRANSLATION_ID='800002_enableLTLRegForSite_BBCD';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='true' WHERE TRANSLATION_ID='800002_enableLTLRegForSite_BBCM';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS SET CONFIG_VALUE='true' WHERE TRANSLATION_ID='800002_enableLTLRegForSite_BBBD';

---- insert config translations : BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS

Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('DC16300001','TBS_BedBathUS','DesktopWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('DefaultStoreType_radius_fis_pdp_BedBathCanada_DesktopWeb','BedBathCanada','DesktopWeb','200','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_challenge_question_flag_BedBathCanada_DesktopWeb','BedBathCanada','DesktopWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_challenge_question_flag_BedBathCanada_MobileWeb','BedBathCanada','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG_BedBathCanada_DesktopWeb','BedBathCanada','DesktopWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_SUPPLY_BALANCE_ON_BedBathCanada_DesktopWeb','BedBathCanada','DesktopWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('DefaultStoreType_radius_fis_pdp_BedBathUS_DesktopWeb','BedBathUS','DesktopWeb','50','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_challenge_question_flag_BedBathUS_DesktopWeb','BedBathUS','DesktopWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_challenge_question_flag_BedBathUS_MobileWeb','BedBathUS','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG_BedBathUS_DesktopWeb','BedBathUS','DesktopWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_SUPPLY_BALANCE_ON_BedBathUS_DesktopWeb','BedBathUS','DesktopWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('DefaultStoreType_radius_fis_pdp_BuyBuyBaby_DesktopWeb','BuyBuyBaby','DesktopWeb','100','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_challenge_question_flag_BuyBuyBaby_DesktopWeb','BuyBuyBaby','DesktopWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_challenge_question_flag_BuyBuyBaby_MobileWeb','BuyBuyBaby','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG_BuyBuyBaby_DesktopWeb','BuyBuyBaby','DesktopWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ThirdPartyURLs_js_BazaarVoice_us_BedBathUS_MobileWeb','BedBathUS','MobileWeb','//bedbathandbeyond.ugc.bazaarvoice.com/static/2009m/bvapi.js','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ThirdPartyURLs_js_BazaarVoice_baby_BuyBuyBaby_MobileWeb','BuyBuyBaby','MobileWeb','//buybuybaby.ugc.bazaarvoice.com/static/8658m/bvapi.js','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ThirdPartyURLs_js_BazaarVoice_ca_BedBathCanada_MobileWeb','BedBathCanada','MobileWeb','//bedbathandbeyond.ugc.bazaarvoice.com/static/2009m/bvapi.js','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_SUPPLY_BALANCE_ON_BuyBuyBaby_DesktopWeb','BuyBuyBaby','DesktopWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ContentCatalogKeys_address_country_code_TBS_BedBathCanada_DesktopWeb','TBS_BedBathCanada','DesktopWeb','ca','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_shipTo_POBoxOn_TBS_BedBathCanada_DesktopWeb','TBS_BedBathCanada','DesktopWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FDF_google_address_switch_TBS_CA_DSK','TBS_BedBathCanada','DesktopWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ContentCatalogKeys_address_country_code_TBS_BedBathUS_DesktopWeb','TBS_BedBathUS','DesktopWeb','us','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_shipTo_POBoxOn_TBS_BedBathUS_DesktopWeb','TBS_BedBathUS','DesktopWeb','True','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FDF_google_address_switch_TBS_BBBY_DSK','TBS_BedBathUS','DesktopWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ContentCatalogKeys_address_country_code_TBS_BuyBuyBaby_DesktopWeb','TBS_BuyBuyBaby','DesktopWeb','us','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_shipTo_POBoxOn_TBS_BuyBuyBaby_DesktopWeb','TBS_BuyBuyBaby','DesktopWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FDF_google_address_switch_TBS_BBB_DSK','TBS_BuyBuyBaby','DesktopWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_notifyRegistrantFlag_CA_M','BedBathCanada','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_google_address_switch_CA_M','BedBathCanada','MobileWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_google_address_switch_CA_DSK','BedBathCanada','DesktopWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ContentCatalogKeys_address_country_code_CA_D','BedBathCanada','DesktopWeb','ca','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ContentCatalogKeys_address_country_code_CA_M','BedBathCanada','MobileWeb','ca','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_Narvar_TrackingFlag_CA_M','BedBathCanada','MobileWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_notifyRegistrantFlag_US_M','BedBathUS','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_google_address_switch_US_M','BedBathUS','MobileWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ContentCatalogKeys_address_country_code_US_M','BedBathUS','MobileWeb','us','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_notifyRegistrantFlag_BBB_M','BuyBuyBaby','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_google_address_switch_BBB_M','BuyBuyBaby','MobileWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_google_address_switch_BBB_D','BuyBuyBaby','DesktopWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ContentCatalogKeys_address_country_code_BBB_D','BuyBuyBaby','DesktopWeb','us','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ContentCatalogKeys_address_country_code_BBB_M','BuyBuyBaby','MobileWeb','us','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_Narvar_TrackingFlag_BBB_D','BuyBuyBaby','DesktopWeb','false','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_college_BBB_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_college_US_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_college_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_findCollege_BBB_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_findCollege_US_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_findCollege_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_gifts_BBB_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_gifts_US_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_gifts_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_circular_BBB_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_circular_US_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_circular_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shippingInfo_BBB_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shippingInfo_US_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shippingInfo_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_privacyPolicy_BBB_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_privacyPolicy_US_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_privacyPolicy_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_viewCreditCard_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_viewCreditCard_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_viewCreditCard_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_updateCreditCard_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_updateCreditCard_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_updateCreditCard_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_topconsultant_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_topconsultant_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_topconsultant_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_storeLocator_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_storeLocator_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_storeLocator_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_storeDetails_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_storeDetails_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_storeDetailsr_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shoppingCart_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shoppingCart_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shoppingCart_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shippingTable_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shippingTable_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shippingTable_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shippingAddressRegisteredUser_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shippingAddressRegisteredUser_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shippingAddressRegisteredUser_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shippingAddressGuest_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shippingAddressGuest_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_shippingAddressGuest_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_selfHeader_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_selfHeader_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_selfHeader_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_searchResult_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_searchResult_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_searchResult_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryShippingAddressRegisteredUser_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryShippingAddressRegisteredUser_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryShippingAddressRegisteredUser_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryShippingAddressGuest_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryShippingAddressGuest_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryShippingAddressGuest_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registrySearchResult_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registrySearchResult_BBB_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registrySearchResult_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryLanding_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryLanding_BBB_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryLanding_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryGifterView_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryGifterView_BBB_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryGifterView_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryComplete_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryComplete_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registryComplete_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registrantView_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registrantView_BBB_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_registrantView_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_productpage_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_productpage_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_productpage_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_privacy_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_privacy_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_privacy_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_previewItems_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_previewItems_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_previewItemsy_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_pickInStore_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_pickInStore_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_pickInStore_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_performSearch_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_performSearch_BBB_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_performSearch_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_orderHistory_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_orderHistory_BBB_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_orderHistory_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_orderDetail_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_orderDetail_BBB_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_orderDetail_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_myAccount_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_myAccount_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_myAccount_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_newUserCreation_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_newUserCreation_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_newUserCreation_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_myregistries_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_myregistriesn_BBB_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_myregistries_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_myAccountCouponsPage_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_myAccountCouponsPage_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_myAccountCouponsPage_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_orderConfirmation_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_orderConfirmation_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_orderConfirmation_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_loginPage_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_loginPage_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_loginPage_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_kickstarters_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_kickstarters_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_kickstarters_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_internationalShippingInfo_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_internationalShippingInfo_BBB_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_internationalShippingInfo_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_home_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_home_BBB_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_home_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_galleryPage_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_galleryPage_BBB_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_galleryPage_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_findOnMap_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_findOnMap_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_findOnMap_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_editAddress_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_editAddressp_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_editAddress_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_easyReturns_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_easyReturns_Baby_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_easyReturns_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_directionOnMap_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_directionOnMap_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_directionOnMap_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_createUserRegisteration_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_createUserRegisteration_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_createUserRegisteration_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_createRegistry_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_createRegistry_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_createRegistry_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_createAccountAfter_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_createAccountAfter_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_createAccountAfter_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_contactUs_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_contactUs_Baby_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_contactUs_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_checkoutSignin_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_checkoutSignin_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_checkoutSignin_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_checkoutShippingPage_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_checkoutShippingPage_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_checkoutShippingPage_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_categorylanding_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_categorylanding_Baby_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_categorylanding_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_categoryNavigation_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_categoryNavigation_Baby_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_categoryNavigation_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_cartCouponsPage_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_cartCouponsPage_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_cartCouponsPage_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_brandlisting_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_brandlisting_Baby_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_brandlistinge_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_brandResult_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_brandResult_Baby_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_brandResult_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_billingInfoTop_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_billingInfoTop_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_billingInfoTop_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_billingInfoAddressRegisteredUser_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_billingInfoAddressRegisteredUser_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_billingInfoAddressRegisteredUser_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_billingInfo_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_billingInfo_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_billingInfo_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_addressBook_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_addressBook_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_addressBook_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_addCreditCard_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_addCreditCard_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_addCreditCard_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_OpinionLabGlobalKey_US_M','BedBathUS','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_OpinionLabGlobalKey_Baby_M','BuyBuyBaby','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_OpinionLabGlobalKey_CA_M','BedBathCanada','MobileWeb','false','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_errorPage_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_errorPage_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_errorPage_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_productDetail_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_productDetail_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_productDetail_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_orderConfirmationIS_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_orderConfirmationIS_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_orderConfirmationIS_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_checkout_US_M','BedBathUS','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_checkout_Baby_M','BuyBuyBaby','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_checkout_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_wishlist_US_M','BedBathUS','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_wishlist_Baby_M','BuyBuyBaby','MobileWeb','pos1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OpinionLabKeys_wishlist_CA_M','BedBathCanada','MobileWeb','hide','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('SinglePageCheckoutOn_US_M','BedBathUS','MobileWeb','FALSE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('SinglePageCheckoutOn_Baby_M','BuyBuyBaby','MobileWeb','FALSE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('SinglePageCheckoutOn_CA_M','BedBathCanada','MobileWeb','FALSE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ContentCatalogKeys_FindAStoreOn_BBB_D','BuyBuyBaby','DesktopWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ContentCatalogKeys_FindAStoreOn_BBB_M','BuyBuyBaby','MobileWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ContentCatalogKeys_FindAStoreOn_BBU_M','BedBathUS','MobileWeb','TRUE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ServiceCacheMapType_getAllLabelForMobile_BBC_M','BedBathCanada','MobileWeb','mobile-labelCache','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ServiceCacheMapType_getAllLabelForMobile_BBU_M','BedBathUS','MobileWeb','mobile-labelCache','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ServiceCacheMapType_getAllLabelForMobile_BBB_M','BuyBuyBaby','MobileWeb','mobile-labelCache','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('GiftRegistryConfig_LastMaintProgram_BBB_D','BuyBuyBaby','DesktopWeb','BAB.COM','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('GiftRegistryConfig_LastMaintProgram_BBC_D','BedBathCanada','DesktopWeb','BBBY.CA','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('CertonaKeys_PDPCustAlsoViewProdMax_BBC_M','BedBathCanada','MobileWeb','10','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('CertonaKeys_PDPCustAlsoViewProdMax_BBU_M','BedBathUS','MobileWeb','10','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('CertonaKeys_PDPCustAlsoViewProdMax_BBB_M','BuyBuyBaby','MobileWeb','10','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('CertonaKeys_HPFunNewProdMax_BBC_M','BedBathCanada','MobileWeb','10','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('CertonaKeys_HPFunNewProdMax_BBU_M','BedBathUS','MobileWeb','10','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('CertonaKeys_HPFunNewProdMax_BBB_M','BuyBuyBaby','MobileWeb','10','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('CertonaKeys_PDPOOSMax_BBC_M','BedBathCanada','MobileWeb','1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('CertonaKeys_PDPOOSMax_BBU_M','BedBathUS','MobileWeb','1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('CertonaKeys_PDPOOSMax_BBB_M','BuyBuyBaby','MobileWeb','1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('CertonaKeys_NOSEARCHMax_BBC_M','BedBathCanada','MobileWeb','10','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('CertonaKeys_NOSEARCHMax_BBU_M','BedBathUS','MobileWeb','10','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('CertonaKeys_NOSEARCHMax_BBB_M','BuyBuyBaby','MobileWeb','10','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OmnitureBoosting_OmnitureReportSuiteId_BBB_D','BuyBuyBaby','DesktopWeb','bbbbuybuybabyprod','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OmnitureBoosting_OmnitureReportSuiteId_BBC_D','BedBathCanada','DesktopWeb','bbbcaprod','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OmnitureBoosting_RecipientTo_BBB_D','BuyBuyBaby','DesktopWeb','noreply@test.com','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OmnitureBoosting_RecipientTo_BBC_D','BedBathCanada','DesktopWeb','noreply@test.com','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OmnitureBoosting_RecipientFrom_BBB_D','BuyBuyBaby','DesktopWeb','noreply@test.com','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('OmnitureBoosting_RecipientFrom_BBC_D ','BedBathCanada','DesktopWeb','noreply@test.com','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ThirdPartyURLs_OracleResponsys_url_BBU_D','BedBathUS','DesktopWeb','https://poc010.rsys2.net/pub/cct?_ri_=X0Gzc2X%3DYQpglLjHJlTQGNGAwohgbUXG8fizgunPWBw6&_ei_=EloXKIDC5KQpGm3YU35Vmwk&','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ThirdPartyURLs_OracleResponsys_url_BBB_D','BuyBuyBaby','DesktopWeb','https://poc010.rsys2.net/pub/cct?_ri_=X0Gzc2X%3DYQpglLjHJlTQGNGAwohgbUXG8fizgunPWBw6&_ei_=EloXKIDC5KQpGm3YU35Vmwk&','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ThirdPartyURLs_OracleResponsys_url_BBC_D','BedBathCanada','DesktopWeb','https://poc010.rsys2.net/pub/cct?_ri_=X0Gzc2X%3DYQpglLjHJlTQGNGAwohgbUXG8fizgunPWBw6&_ei_=EloXKIDC5KQpGm3YU35Vmwk&','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ThirdPartyURLs_OracleResponsys_url_BBU_M','BedBathUS','MobileWeb','https://poc010.rsys2.net/pub/cct?_ri_=X0Gzc2X%3DYQpglLjHJlTQGNGAwohgbUXG8fizgunPWBw6&_ei_=EloXKIDC5KQpGm3YU35Vmwk&','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ThirdPartyURLs_OracleResponsys_url_BBB_M','BuyBuyBaby','MobileWeb','https://poc010.rsys2.net/pub/cct?_ri_=X0Gzc2X%3DYQpglLjHJlTQGNGAwohgbUXG8fizgunPWBw6&_ei_=EloXKIDC5KQpGm3YU35Vmwk&','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('ThirdPartyURLs_OracleResponsys_url_BBC_M','BedBathCanada','MobileWeb','https://poc010.rsys2.net/pub/cct?_ri_=X0Gzc2X%3DYQpglLjHJlTQGNGAwohgbUXG8fizgunPWBw6&_ei_=EloXKIDC5KQpGm3YU35Vmwk&','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_ResponsysEnabled_BBU_D','BedBathUS','DesktopWeb','FALSE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_ResponsysEnabled_BBB_D','BuyBuyBaby','DesktopWeb','FALSE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_ResponsysEnabled_BBC_D','BedBathCanada','DesktopWeb','FALSE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_ResponsysEnabled_BBU_M','BedBathUS','MobileWeb','FALSE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_ResponsysEnabled_BBB_M','BuyBuyBaby','MobileWeb','FALSE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_ResponsysEnabled_BBC_M','BedBathCanada','MobileWeb','FALSE','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG_BABY_M','BuyBuyBaby','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG_US_M','BedBathUS','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG_CA_M','BedBathCanada','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_SUPPLY_BALANCE_ON_US_M','BedBathUS','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_SUPPLY_BALANCE_ON_CA_M','BedBathCanada','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_SUPPLY_BALANCE_ON_BABY_M','BuyBuyBaby','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_hoorayModalOn_M_BABY','BuyBuyBaby','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_hoorayModalOn_M_CA','BedBathCanada','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_hoorayModalOn_M_US','BedBathUS','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('DefaultStoreId_TBS_Baby','TBS_BuyBuyBaby','DesktopWeb','3651','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('DefaultStoreId_TBS_US','TBS_BedBathUS','DesktopWeb','651','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('DefaultStoreId_TBS_Canada','TBS_BedBathCanada','DesktopWeb','2291','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('GiftRegistryConfig_DefaultRegistryType_BBB_D','BuyBuyBaby','DesktopWeb','BA1','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_enableOPBBoosting_DSK_US','BedBathUS','DesktopWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_enableOPBBoosting_DSK_BAB','BuyBuyBaby','DesktopWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_enableOPBBoosting_DSK_CA','BedBathCanada','DesktopWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_enableOPBBoosting_MOB_US','BedBathUS','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_enableOPBBoosting_MOB_BAB','BuyBuyBaby','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('FlagDrivenFunctions_enableOPBBoosting_MOB_CA','BedBathCanada','MobileWeb','true','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('MobileWebConfig_requestDomainName_US_MA','BedBathUS','MobileApp','www.bedbathandbeyond.com','');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS_TRANSLATIONS (TRANSLATION_ID,SITE_ID,CHANNEL_ID,CONFIG_VALUE,CONFIG_KEY) values ('MobileWebConfig_requestDomainName_BBB_MA','BuyBuyBaby','MobileApp','www.buybuybaby.com','');

--- update configs : BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE

UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='//buybuybaby.ugc.bazaarvoice.com/static/8658-en_us/bvapi.js' WHERE CONFIG_KEY_ID='300002js_BazaarVoice_baby';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='false' WHERE CONFIG_KEY_ID='500001MapQuestTag_tbs_ca';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='TRUE' WHERE CONFIG_KEY_ID='500001MapQuestTag_tbs_us';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='True' WHERE CONFIG_KEY_ID='BBBPSI5CK00007';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='FALSE' WHERE CONFIG_KEY_ID='BBBPSI5CK00030';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='' WHERE CONFIG_KEY_ID='BBBPSI6CKS10008';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='6020121326763' WHERE CONFIG_KEY_ID='BBBPSI6CKS10010';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='TRUE' WHERE CONFIG_KEY_ID='800002_SinglePageCheckoutOn';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='5000' WHERE CONFIG_KEY_ID='DC1900018SOCKET_TIME_OUT';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='2.08.19.004' WHERE CONFIG_KEY_ID='PUB2300005';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='7' WHERE CONFIG_KEY_ID='DC11100008HPFunNewProdMax';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='15' WHERE CONFIG_KEY_ID='DC11100008PDPCustAlsoViewProdMax';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='1' WHERE CONFIG_KEY_ID='BBBPSI6CKS10001';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='exitemid::context::userid::number' WHERE CONFIG_KEY_ID='DC11000015certona_nosearch_rr';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='exitemid::context::userid::number' WHERE CONFIG_KEY_ID='DC11000014certona_nosearch_rr';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='exitemid::context::userid::number' WHERE CONFIG_KEY_ID='DC11000016certona_nosearch_rr';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='651' WHERE CONFIG_KEY_ID='DC11200006DefaultStoreId';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='300000' WHERE CONFIG_KEY_ID='500001_COHERENCE_ENABLE_THRESHOLD_TIME';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='true' WHERE CONFIG_KEY_ID='800002_enableLTLRegForSite';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='3' WHERE CONFIG_KEY_ID='BBBMARPSCKV100003';
UPDATE BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE SET CONFIG_VALUE='true' WHERE CONFIG_KEY_ID='800002_enableLTLRegForSite';

---insert configs : BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE

Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('CartAndCheckoutKeys_AdditionShipping_StateCodesList','AdditionShipping_StateCodesList','AR,HW,PR');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('CertonaKeys_certona_quick_view_scheme','certona_quick_view_scheme','quickview');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('CertonaKeys_certona_search_pdt_count','certona_search_pdt_count','15');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('CertonaKeys_certona_search_scheme','certona_search_scheme','search_rr');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ContentCatalogKey_bigBlueToken','bigBlueToken','1223334444');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ContentCatalogKeys_address_country_code','address_country_code','us');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ContentCatalogKeys_mobileSmartSEOOn','mobileSmartSEOOn','TRUE');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ContentCatalogKeys_scene7EndecaImageSize','scene7EndecaImageSize','400');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('DefaultStoreType_radius_fis_pdp','radius_fis_pdp','25');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('DimNonDisplayConfig_Eligible_Customizations','Eligible_Customizations','Eligible Customizations');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('FlagDrivenFunctions_challenge_question_flag','challenge_question_flag','true');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('FlagDrivenFunctions_google_address_switch','google_address_switch','TRUE');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG','LOCAL_STORE_PDP_FLAG','true');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('FlagDrivenFunctions_Narvar_TrackingFlag','Narvar_TrackingFlag','true');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('FlagDrivenFunctions_notifyRegistrantFlag','notifyRegistrantFlag','true');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('FlagDrivenFunctions_SUPPLY_BALANCE_ON','SUPPLY_BALANCE_ON','true');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_BabyValidRegTypes','BabyValidRegTypes','BA1,BR1,OT1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_BbbyValidRegTypes','BbbyValidRegTypes','BRD,COM,ANN,HSW,COL,BIR,RET,PNH,OTH,BA1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_CAValidRegTypes','CAValidRegTypes','BRD,COM,ANN,HSW,COL,BIR,RET,PNH,OTH,BA1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_LogErrors','LogErrors','Y');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_maxSkuLength','maxSkuLength','8');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_minSkuLength','minSkuLength','8');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_PROCESS_CD','PROCESS_CD','ATG');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_ShowDetailedErrorInResponse','ShowDetailedErrorInResponse','Y');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_STATE_ABBREVATION','STATE_ABBREVATION',':AL:AK:AZ:AR:CA:CO:CT:DE:DC:FL:GA:HI:ID:IL:IN:IA:KS:KY:LA:ME:MD:MA:MI:MN:MS:MO:MT:NE:NV:NH:NJ:NM:NY:NC:ND:OH:OK:OR:PA:PR:RI:SC:SD:TN:TX:UT:VT:VA:WA:WV:WI:WY:AA:AE:AP:');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_STATE_ABBREVATION_CA','STATE_ABBREVATION_CA',':AB:BC:MB:NB:NL:NT:NS:NU:ON:PE:SK:YT:QC:');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('HTTPClientOptParams_BedBathCanada_certona_quickview','certona_quickview','exitemid::context::userid');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('HTTPClientOptParams_BedBathCanada_certona_search_rr','certona_search_rr','exitemid::context::userid::number');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('HTTPClientOptParams_BedBathUS_certona_quickview','certona_quickview','exitemid::context::userid');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('HTTPClientOptParams_BedBathUS_certona_search_rr','certona_search_rr','exitemid::context::userid::number');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('HTTPClientOptParams_BuyBuyBaby_certona_quickview','certona_quickview','exitemid::context::userid');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('HTTPClientOptParams_BuyBuyBaby_certona_search_rr','certona_search_rr','exitemid::context::userid::number');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('HTTPClientReqParams_BedBathCanada_certona_quickview','certona_quickview','appid::scheme::sessionid::trackingid::ipaddress::productId');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('HTTPClientReqParams_BedBathCanada_certona_search_rr','certona_search_rr','appid::scheme::sessionid::trackingid::ipaddress::searchterms::output');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('HTTPClientReqParams_BedBathUS_certona_quickview','certona_quickview','appid::scheme::sessionid::trackingid::ipaddress::productId');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('HTTPClientReqParams_BedBathUS_certona_search_rr','certona_search_rr','appid::scheme::sessionid::trackingid::ipaddress::searchterms::output');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('HTTPClientReqParams_BuyBuyBaby_certona_quickview','certona_quickview','appid::scheme::sessionid::trackingid::ipaddress::productId');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('HTTPClientReqParams_BuyBuyBaby_certona_search_rr','certona_search_rr','appid::scheme::sessionid::trackingid::ipaddress::searchterms::output');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('International_Shipping_po_decrypt_pgp_file_path','po_decrypt_pgp_file_path','/export/atg/common/commonName.properties');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('invLocalStoreRepoSchedulerConfigValues_RecipientFrom','RecipientFrom','vnalini@sapient.com');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('invLocalStoreRepoSchedulerConfigValues_RecipientTo','RecipientTo','vnalini@sapient.com, vnalini@sapient.com,simran2@sapient.com');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('NotifyRegistrant_daysOffthreshold','daysOffthreshold','10');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('NotifyRegistrant_global_InventoryThreshold','global_InventoryThreshold','50');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_OmnitureAPISecretKey','OmnitureAPISecretKey','ad9328a2585f5deb7c041a6d7cfbb4c6');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_OmnitureAPIUserName','OmnitureAPIUserName','bbbywebmaster:Bed Bath Beyond');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_OmnitureEndPointURL','OmnitureEndPointURL','https://api.omniture.com/admin/1.4/rest/?');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_OmnitureReportDaysFrom','OmnitureReportDaysFrom','30');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_OmnitureReportDaysTo','OmnitureReportDaysTo','0');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_OmnitureReportGet','OmnitureReportGet','Report.Get');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_OmnitureReportMetricId','OmnitureReportMetricId','cm300001754_564ceca4e4b0e32032eb50df');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_OmnitureReportQueue','OmnitureReportQueue','Report.Queue');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_OmnitureReportRetentionDays','OmnitureReportRetentionDays','7');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_OmnitureTopSearchedTerms','OmnitureTopSearchedTerms','4');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_OmnitureTotalSearchedTerms','OmnitureTotalSearchedTerms','10003');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_college','college','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_findCollege','findCollege','pos2');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_gifts','gifts','pos2');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_circular','circular','pos2');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_shippingInfo','shippingInfo','pos2');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_privacyPolicy','privacyPolicy','pos2');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_brandlisting','brandlisting','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_brandResult','brandResult','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_checkout','checkout','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_errorPage','errorPage','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_internationalShippingInfo','internationalShippingInfo','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_kickstarters','kickstarters','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_OpinionLabGlobalKey','OpinionLabGlobalKey','TRUE');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_orderConfirmationIS','orderConfirmationIS','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_productDetail','productDetail','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_searchResult','searchResult','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_topconsultant','topconsultant','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_wishlist','wishlist','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ServiceCacheMapType_getLeftNavigation','getLeftNavigation','mobile-getLeftNavigation');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ServiceCacheMapType_getRestStoreDetails','getRestStoreDetails','mobile-storeDetailsCache');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ThirdPartyURLs_CookieLatLng','CookieLatLng','86400');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ThirdPartyURLs_mapQuestSearchStringForLatLng','mapQuestSearchStringForLatLng','http://www.mapquestapi.com/geocoding/v1/address?key=Gmjtd%7Clu6120u8nh%2C2w%3Do5-lwt2l&');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ThirdPartyURLS_Narvar_Track_Num_Param','Narvar_Track_Num_Param','?tracking_numbers=');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ThirdPartyURLS_Narvar_TrackingUrl','Narvar_TrackingUrl','http://bedbathandbeyond.narvar.com/bedbathandbeyond/tracking/');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ValueLinkKeys_MwkKeys','MwkKeys','e65d3e628975f79ed0626de52a6d891fe65d3e628975f79e');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegisrty_BBBWebServiceLogin_createRegistry2','BBBWebServiceLogin_createRegistry2','bbbyproof');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegisrty_BBBWebServiceLogin_getProfile','BBBWebServiceLogin_getProfile','bbbyproof');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegisrty_BBBWebServiceLogin_getRegistryInfo2','BBBWebServiceLogin_getRegistryInfo2','bbbyproof');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegisrty_BBBWebServiceLogin_updateRegistry2','BBBWebServiceLogin_updateRegistry2','bbbyproof');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegisrty_BBBWebServicePwd_createRegistry2','BBBWebServicePwd_createRegistry2','Un10nNJ3');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegisrty_BBBWebServicePwd_getProfile','BBBWebServicePwd_getProfile','Un10nNJ3');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegisrty_BBBWebServicePwd_getRegistryInfo2','BBBWebServicePwd_getRegistryInfo2','Un10nNJ3');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegisrty_BBBWebServicePwd_updateRegistry2','BBBWebServicePwd_updateRegistry2','Un10nNJ3');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegisrty_SFL_MAX_LIMIT','SFL_MAX_LIMIT','1000000');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegisrty_WSEndPoint_createRegistry2','WSEndPoint_createRegistry2','http://development/atgws/eservices.asmx');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegisrty_WSEndPoint_getRegistryInfo2','WSEndPoint_getRegistryInfo2','http://ecomqa/atgws/eservices.asmx');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegisrty_WSEndPoint_updateRegistry2','WSEndPoint_updateRegistry2','http://development/atgws/eservices.asmx');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegistry_WSEndPoint_getProfile','WSEndPoint_getProfile','http://ecomdev//atgws/eservices.asmx');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSStubClasses_createRegistry2','createRegistry2','com.bedbathandbeyond.www.EservicesStub');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSStubClasses_getProfile ','getProfile','com.bedbathandbeyond.www.EservicesStub');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSStubClasses_getRegistryInfo2','getRegistryInfo2','com.bedbathandbeyond.www.EservicesStub');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSStubClasses_updateRegistry2','updateRegistry2','com.bedbathandbeyond.www.EservicesStub');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ContentCatalogKeys_FindAStoreOn','FindAStoreOn','TRUE');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ServiceCacheMapType_getAllLabelForMobile','getAllLabelForMobile','mobile-labelCache');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_LastMaintProgram','LastMaintProgram','BBBY.COM');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('CertonaKeys_NOSEARCHMax','NOSEARCHMax','15');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('WSDL_GiftRegisrty_CART_MAX_LIMIT','CART_MAX_LIMIT','1000');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('BBBAOIPP99999','PerPage','48');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('BBBAOIRS99999','ResultSetSize','500');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('BBBAOIDR99999','DateRange','21');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_OmnitureReportSuiteId','OmnitureReportSuiteId','bbbprod');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_RecipientTo','RecipientTo','noreply@test.com');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OmnitureBoosting_RecipientFrom','RecipientFrom','noreply@test.com');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ThirdPartyURLs_LocalStoreRepoFetch','LocalStoreRepoFetch','false');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ThirdPartyURLs_OracleResponsys_url','OracleResponsys_url','https://poc010.rsys2.net/pub/cct?_ri_=X0Gzc2X%3DYQpglLjHJlTQGNGAwohgbUXG8fizgunPWBw6&_ei_=EloXKIDC5KQpGm3YU35Vmwk&');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('FlagDrivenFunctions_ResponsysEnabled','ResponsysEnabled','FALSE');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ContentCatalogKeys_ResponsysCookieName','ResponsysCookieName','responsys');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ServiceCacheMapType_getRestStoreDetailsVO','getRestStoreDetailsVO','mobile-storeDetailsCacheVO');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('invLocalStoreRepoSchedulerConfigValues_PoolSize','PoolSize','2');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('FlagDrivenFunctions_enableOPBBoosting','enableOPBBoosting','true');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('FlagDrivenFunctions_registryOwnerViewShowTopPromoSlots','registry_owner_view_show_top_promo_slots','true');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('FlagDrivenFunctions_enableUGCQV','enableUGCQV','TRUE');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ThirdPartyURLs_mxFooter_emailSignUpLink','mxFooter_emailSignUpLink','https://app.bedbathandbeyond.mx/enroll/signup.cfm');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ContentCatalogKeys_maxEDWRepoRetry','maxEDWRepoRetry','6');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ContentCatalogKeys_EDW_TTL','EDW_TTL','7');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('BBBJMSObject_EDWProfileData','EDWProfileDataDestinationJndi','topic/EDWProfileDataTopic');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('FlagDrivenFunctions_ship_promo_banner_flag_cart','ship_promo_banner_flag_cart','TRUE');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_DC1ROWXNGUSER','DC1ROWXNGUSER','ATGEAST');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_DC2ROWXNGUSER','DC2ROWXNGUSER','ATGWEST');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_DC3ROWXNGUSER','DC3ROWXNGUSER','ATGEAST');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_ROWXNGUSER','ROWXNGUSER','ATGEAST');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_STG1ROWXNGUSER','STG1ROWXNGUSER','ATGEAST');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_STG2ROWXNGUSER','STG2ROWXNGUSER','ATGWEST');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_STG3ROWXNGUSER','STG3ROWXNGUSER','ATGEAST');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('FlagDrivenFunctions_hoorayModalOn','hoorayModalOn','TRUE');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('FlagDrivenFunctions_ship_promo_banner_flag_pdp','ship_promo_banner_flag_pdp','FALSE');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('GiftRegistryConfig_DefaultRegistryType','DefaultRegistryType','BRD');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('EDWfields_is_tibco_enabled','is_tibco_enabled','TRUE');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('ContentCatalogKey_order_confirm_mcid_param_email','order_confirm_mcid_param_email','EM_triggeredem_orderconfirm__allpromos');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('DimDisplayConfig_Ratings','Ratings','RATINGS');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_addCreditCard','addCreditCard','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_addressBook','addressBook','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_billingInfo','billingInfo','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_billingInfoAddressRegisteredUser','billingInfoAddressRegisteredUser','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_billingInfoTop','billingInfoTop','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_cartCouponsPage','cartCouponsPage','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_categorylanding','categorylanding','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_categoryNavigation','categoryNavigation','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_checkoutShippingPage','checkoutShippingPage','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_checkoutSignin','checkoutSignin','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_contactUs','contactUs','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_createAccountAfter','createAccountAfter','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_createRegistry','createRegistry','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_createUserRegisteration','createUserRegisteration','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_directionOnMap','directionOnMap','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_easyReturns','easyReturns','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_editAddress','editAddress','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_findOnMap','findOnMap','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_galleryPage','galleryPage','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_home','home','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_loginPage','loginPage','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_myAccount','myAccount','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_myAccountCouponsPage','myAccountCouponsPage','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_myregistries','myregistries','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_newUserCreation','newUserCreation','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_orderConfirmation','orderConfirmation','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_orderDetail','orderDetail','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_orderHistory','orderHistory','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_performSearch','performSearch','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_pickInStore','pickInStore','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_previewItems','previewItems','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_privacy','privacy','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_productpage','productpage','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_registrantView','registrantView','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_registryComplete','registryComplete','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_registryGifterView','registryGifterView','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_registryLanding','registryLanding','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_registrySearchResult','registrySearchResult','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_registryShippingAddressGuest','registryShippingAddressGuest','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_registryShippingAddressRegisteredUser','registryShippingAddressRegisteredUser','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_selfHeader','selfHeader','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_shippingAddressGuest','shippingAddressGuest','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_shippingAddressRegisteredUser','shippingAddressRegisteredUser','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_shippingTable','shippingTable','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_shoppingCart','shoppingCart','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_storeDetails','storeDetails','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_storeLocator','storeLocator','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_updateCreditCard','updateCreditCard','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('OpinionLabKeys_viewCreditCard','viewCreditCard','pos1');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE (CONFIG_KEY_ID,CONFIG_KEY,CONFIG_VALUE) values ('HTMLCacheKeys_FlyoutCacheTimeoutTbs','FlyoutCacheTimeoutTbs','28800');

--- insert translation-key relationship : BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN

Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ContentCatalogKeys_address_country_code_TBS_BedBathCanada_DesktopWeb','ContentCatalogKeys_address_country_code');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ContentCatalogKeys_address_country_code_BBB_M','ContentCatalogKeys_address_country_code');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ContentCatalogKeys_address_country_code_CA_D','ContentCatalogKeys_address_country_code');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ContentCatalogKeys_address_country_code_BBB_D','ContentCatalogKeys_address_country_code');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ContentCatalogKeys_address_country_code_CA_M','ContentCatalogKeys_address_country_code');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ContentCatalogKeys_address_country_code_TBS_BuyBuyBaby_DesktopWeb','ContentCatalogKeys_address_country_code');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ContentCatalogKeys_address_country_code_TBS_BedBathUS_DesktopWeb','ContentCatalogKeys_address_country_code');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('DefaultStoreType_radius_fis_pdp_BuyBuyBaby_DesktopWeb','DefaultStoreType_radius_fis_pdp');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('DefaultStoreType_radius_fis_pdp_BedBathUS_DesktopWeb','DefaultStoreType_radius_fis_pdp');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('DefaultStoreType_radius_fis_pdp_BedBathCanada_DesktopWeb','DefaultStoreType_radius_fis_pdp');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_challenge_question_flag_BedBathUS_DesktopWeb','FlagDrivenFunctions_challenge_question_flag');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_challenge_question_flag_BedBathCanada_DesktopWeb','FlagDrivenFunctions_challenge_question_flag');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_challenge_question_flag_BuyBuyBaby_MobileWeb','FlagDrivenFunctions_challenge_question_flag');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_challenge_question_flag_BedBathCanada_MobileWeb','FlagDrivenFunctions_challenge_question_flag');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_challenge_question_flag_BuyBuyBaby_DesktopWeb','FlagDrivenFunctions_challenge_question_flag');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_challenge_question_flag_BedBathUS_MobileWeb','FlagDrivenFunctions_challenge_question_flag');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FDF_google_address_switch_TBS_BBBY_DSK','FlagDrivenFunctions_google_address_switch');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FDF_google_address_switch_TBS_BBB_DSK','FlagDrivenFunctions_google_address_switch');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FDF_google_address_switch_TBS_CA_DSK','FlagDrivenFunctions_google_address_switch');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_google_address_switch_BBB_D','FlagDrivenFunctions_google_address_switch');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_google_address_switch_CA_DSK','FlagDrivenFunctions_google_address_switch');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_google_address_switch_BBB_M','FlagDrivenFunctions_google_address_switch');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_google_address_switch_US_M','FlagDrivenFunctions_google_address_switch');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_google_address_switch_CA_M','FlagDrivenFunctions_google_address_switch');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG_BedBathUS_DesktopWeb','FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG_BuyBuyBaby_DesktopWeb','FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG_BedBathCanada_DesktopWeb','FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG_BABY_M','FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG_US_M','FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG_CA_M','FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_Narvar_TrackingFlag_CA_M','FlagDrivenFunctions_Narvar_TrackingFlag');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_Narvar_TrackingFlag_BBB_D','FlagDrivenFunctions_Narvar_TrackingFlag');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_notifyRegistrantFlag_CA_M','FlagDrivenFunctions_notifyRegistrantFlag');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_notifyRegistrantFlag_BBB_M','FlagDrivenFunctions_notifyRegistrantFlag');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_notifyRegistrantFlag_US_M','FlagDrivenFunctions_notifyRegistrantFlag');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_SUPPLY_BALANCE_ON_BedBathCanada_DesktopWeb','FlagDrivenFunctions_SUPPLY_BALANCE_ON');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_SUPPLY_BALANCE_ON_BuyBuyBaby_DesktopWeb','FlagDrivenFunctions_SUPPLY_BALANCE_ON');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_SUPPLY_BALANCE_ON_BedBathUS_DesktopWeb','FlagDrivenFunctions_SUPPLY_BALANCE_ON');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_SUPPLY_BALANCE_ON_US_M','FlagDrivenFunctions_SUPPLY_BALANCE_ON');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_SUPPLY_BALANCE_ON_CA_M','FlagDrivenFunctions_SUPPLY_BALANCE_ON');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_SUPPLY_BALANCE_ON_BABY_M','FlagDrivenFunctions_SUPPLY_BALANCE_ON');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_college_BBB_M','OpinionLabKeys_college');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_college_US_M','OpinionLabKeys_college');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_college_Baby_M','OpinionLabKeys_college');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_college_CA_M','OpinionLabKeys_college');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_findCollege_US_M','OpinionLabKeys_findCollege');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_findCollege_Baby_M','OpinionLabKeys_findCollege');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_findCollege_CA_M','OpinionLabKeys_findCollege');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_gifts_US_M','OpinionLabKeys_gifts');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_gifts_Baby_M','OpinionLabKeys_gifts');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_gifts_CA_M','OpinionLabKeys_gifts');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_circular_US_M','OpinionLabKeys_circular');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_circular_Baby_M','OpinionLabKeys_circular');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_circular_CA_M','OpinionLabKeys_circular');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shippingInfo_US_M','OpinionLabKeys_shippingInfo');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shippingInfo_Baby_M','OpinionLabKeys_shippingInfo');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shippingInfo_CA_M','OpinionLabKeys_shippingInfo');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_privacyPolicy_US_M','OpinionLabKeys_privacyPolicy');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_privacyPolicy_Baby_M','OpinionLabKeys_privacyPolicy');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_privacyPolicy_CA_M','OpinionLabKeys_privacyPolicy');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_brandlisting_US_M','OpinionLabKeys_brandlisting');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_brandlisting_Baby_M','OpinionLabKeys_brandlisting');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_brandlistinge_CA_M','OpinionLabKeys_brandlisting');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_brandResult_US_M','OpinionLabKeys_brandResult');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_brandResult_Baby_M','OpinionLabKeys_brandResult');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_brandResult_CA_M','OpinionLabKeys_brandResult');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_checkout_US_M','OpinionLabKeys_checkout');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_checkout_Baby_M','OpinionLabKeys_checkout');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_checkout_CA_M','OpinionLabKeys_checkout');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_errorPage_US_M','OpinionLabKeys_errorPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_errorPage_Baby_M','OpinionLabKeys_errorPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_errorPage_CA_M','OpinionLabKeys_errorPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_internationalShippingInfo_US_M','OpinionLabKeys_internationalShippingInfo');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_internationalShippingInfo_BBB_M','OpinionLabKeys_internationalShippingInfo');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_internationalShippingInfo_CA_M','OpinionLabKeys_internationalShippingInfo');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_kickstarters_US_M','OpinionLabKeys_kickstarters');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_kickstarters_BBB_M','OpinionLabKeys_kickstarters');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_kickstarters_CA_M','OpinionLabKeys_kickstarters');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_OpinionLabGlobalKey_US_M','OpinionLabKeys_OpinionLabGlobalKey');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_OpinionLabGlobalKey_Baby_M','OpinionLabKeys_OpinionLabGlobalKey');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_OpinionLabGlobalKey_CA_M','OpinionLabKeys_OpinionLabGlobalKey');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_orderConfirmationIS_US_M','OpinionLabKeys_orderConfirmationIS');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_orderConfirmationIS_Baby_M','OpinionLabKeys_orderConfirmationIS');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_orderConfirmationIS_CA_M','OpinionLabKeys_orderConfirmationIS');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_productDetail_US_M','OpinionLabKeys_productDetail');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_productDetail_Baby_M','OpinionLabKeys_productDetail');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_productDetail_CA_M','OpinionLabKeys_productDetail');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_searchResult_US_M','OpinionLabKeys_searchResult');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_searchResult_BBB_M','OpinionLabKeys_searchResult');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_searchResult_CA_M','OpinionLabKeys_searchResult');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_topconsultant_US_M','OpinionLabKeys_topconsultant');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_topconsultant_BBB_M','OpinionLabKeys_topconsultant');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_topconsultant_CA_M','OpinionLabKeys_topconsultant');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_wishlist_US_M','OpinionLabKeys_wishlist');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_wishlist_Baby_M','OpinionLabKeys_wishlist');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_wishlist_CA_M','OpinionLabKeys_wishlist');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ContentCatalogKeys_FindAStoreOn_BBB_D','ContentCatalogKeys_FindAStoreOn');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ContentCatalogKeys_FindAStoreOn_BBB_M','ContentCatalogKeys_FindAStoreOn');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ContentCatalogKeys_FindAStoreOn_BBU_M','ContentCatalogKeys_FindAStoreOn');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ServiceCacheMapType_getAllLabelForMobile_BBC_M','ServiceCacheMapType_getAllLabelForMobile');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ServiceCacheMapType_getAllLabelForMobile_BBU_M','ServiceCacheMapType_getAllLabelForMobile');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ServiceCacheMapType_getAllLabelForMobile_BBB_M','ServiceCacheMapType_getAllLabelForMobile');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('GiftRegistryConfig_LastMaintProgram_BBB_D','GiftRegistryConfig_LastMaintProgram');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('GiftRegistryConfig_LastMaintProgram_BBC_D','GiftRegistryConfig_LastMaintProgram');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('CertonaKeys_NOSEARCHMax_BBC_M','CertonaKeys_NOSEARCHMax');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('CertonaKeys_NOSEARCHMax_BBU_M','CertonaKeys_NOSEARCHMax');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('CertonaKeys_NOSEARCHMax_BBB_M','CertonaKeys_NOSEARCHMax');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OmnitureBoosting_OmnitureReportSuiteId_BBB_D','OmnitureBoosting_OmnitureReportSuiteId');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OmnitureBoosting_OmnitureReportSuiteId_BBC_D','OmnitureBoosting_OmnitureReportSuiteId');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OmnitureBoosting_RecipientTo_BBB_D','OmnitureBoosting_RecipientTo');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OmnitureBoosting_RecipientTo_BBC_D','OmnitureBoosting_RecipientTo');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OmnitureBoosting_RecipientFrom_BBB_D','OmnitureBoosting_RecipientFrom');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OmnitureBoosting_RecipientFrom_BBC_D','OmnitureBoosting_RecipientFrom');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ThirdPartyURLs_OracleResponsys_url_BBU_D','ThirdPartyURLs_OracleResponsys_url');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ThirdPartyURLs_OracleResponsys_url_BBB_D','ThirdPartyURLs_OracleResponsys_url');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ThirdPartyURLs_OracleResponsys_url_BBC_D','ThirdPartyURLs_OracleResponsys_url');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ThirdPartyURLs_OracleResponsys_url_BBU_M','ThirdPartyURLs_OracleResponsys_url');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ThirdPartyURLs_OracleResponsys_url_BBB_M','ThirdPartyURLs_OracleResponsys_url');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ThirdPartyURLs_OracleResponsys_url_BBC_M','ThirdPartyURLs_OracleResponsys_url');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_ResponsysEnabled_BBU_D','FlagDrivenFunctions_ResponsysEnabled');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_ResponsysEnabled_BBB_D','FlagDrivenFunctions_ResponsysEnabled');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_ResponsysEnabled_BBC_D','FlagDrivenFunctions_ResponsysEnabled');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_ResponsysEnabled_BBU_M','FlagDrivenFunctions_ResponsysEnabled');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_ResponsysEnabled_BBB_M','FlagDrivenFunctions_ResponsysEnabled');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_ResponsysEnabled_BBC_M','FlagDrivenFunctions_ResponsysEnabled');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_hoorayModalOn_M_BABY','FlagDrivenFunctions_hoorayModalOn');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_hoorayModalOn_M_CA','FlagDrivenFunctions_hoorayModalOn');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_hoorayModalOn_M_US','FlagDrivenFunctions_hoorayModalOn');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('GiftRegistryConfig_DefaultRegistryType_BBB_D','GiftRegistryConfig_DefaultRegistryType');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ThirdPartyURLs_js_BazaarVoice_us_BedBathUS_MobileWeb','300002js_BazaarVoice_us');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ThirdPartyURLs_js_BazaarVoice_baby_BuyBuyBaby_MobileWeb','300002js_BazaarVoice_baby');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('ThirdPartyURLs_js_BazaarVoice_ca_BedBathCanada_MobileWeb','300002js_BazaarVoice_ca');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_shipTo_POBoxOn_TBS_BedBathUS_DesktopWeb','BBBPSI5CK00007');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_shipTo_POBoxOn_TBS_BedBathCanada_DesktopWeb','BBBPSI5CK00007');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_shipTo_POBoxOn_TBS_BuyBuyBaby_DesktopWeb','BBBPSI5CK00007');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_addCreditCard_US_M','OpinionLabKeys_addCreditCard');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_addCreditCard_Baby_M','OpinionLabKeys_addCreditCard');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_addCreditCard_CA_M','OpinionLabKeys_addCreditCard');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_addressBook_US_M','OpinionLabKeys_addressBook');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_addressBook_Baby_M','OpinionLabKeys_addressBook');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_addressBook_CA_M','OpinionLabKeys_addressBook');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_billingInfo_US_M','OpinionLabKeys_billingInfo');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_billingInfo_Baby_M','OpinionLabKeys_billingInfo');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_billingInfo_CA_M','OpinionLabKeys_billingInfo');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_billingInfoAddressRegisteredUser_US_M','OpinionLabKeys_billingInfoAddressRegisteredUser');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_billingInfoAddressRegisteredUser_Baby_M','OpinionLabKeys_billingInfoAddressRegisteredUser');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_billingInfoAddressRegisteredUser_CA_M','OpinionLabKeys_billingInfoAddressRegisteredUser');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_billingInfoTop_US_M','OpinionLabKeys_billingInfoTop');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_billingInfoTop_Baby_M','OpinionLabKeys_billingInfoTop');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_billingInfoTop_CA_M','OpinionLabKeys_billingInfoTop');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_cartCouponsPage_US_M','OpinionLabKeys_cartCouponsPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_cartCouponsPage_Baby_M','OpinionLabKeys_cartCouponsPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_cartCouponsPage_CA_M','OpinionLabKeys_cartCouponsPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_categorylanding_US_M','OpinionLabKeys_categorylanding');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_categorylanding_Baby_M','OpinionLabKeys_categorylanding');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_categorylanding_CA_M','OpinionLabKeys_categorylanding');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_categoryNavigation_US_M','OpinionLabKeys_categoryNavigation');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_categoryNavigation_Baby_M','OpinionLabKeys_categoryNavigation');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_categoryNavigation_CA_M','OpinionLabKeys_categoryNavigation');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_checkoutShippingPage_US_M','OpinionLabKeys_checkoutShippingPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_checkoutShippingPage_Baby_M','OpinionLabKeys_checkoutShippingPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_checkoutShippingPage_CA_M','OpinionLabKeys_checkoutShippingPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_checkoutSignin_US_M','OpinionLabKeys_checkoutSignin');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_checkoutSignin_Baby_M','OpinionLabKeys_checkoutSignin');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_checkoutSignin_CA_M','OpinionLabKeys_checkoutSignin');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_contactUs_US_M','OpinionLabKeys_contactUs');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_contactUs_Baby_M','OpinionLabKeys_contactUs');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_contactUs_CA_M','OpinionLabKeys_contactUs');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_createAccountAfter_US_M','OpinionLabKeys_createAccountAfter');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_createAccountAfter_Baby_M','OpinionLabKeys_createAccountAfter');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_createAccountAfter_CA_M','OpinionLabKeys_createAccountAfter');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_createRegistry_US_M','OpinionLabKeys_createRegistry');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_createRegistry_Baby_M','OpinionLabKeys_createRegistry');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_createRegistry_CA_M','OpinionLabKeys_createRegistry');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_createUserRegisteration_US_M','OpinionLabKeys_createUserRegisteration');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_createUserRegisteration_Baby_M','OpinionLabKeys_createUserRegisteration');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_createUserRegisteration_CA_M','OpinionLabKeys_createUserRegisteration');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_directionOnMap_US_M','OpinionLabKeys_directionOnMap');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_directionOnMap_Baby_M','OpinionLabKeys_directionOnMap');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_directionOnMap_CA_M','OpinionLabKeys_directionOnMap');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_easyReturns_US_M','OpinionLabKeys_easyReturns');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_easyReturns_Baby_M','OpinionLabKeys_easyReturns');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_easyReturns_CA_M','OpinionLabKeys_easyReturns');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_editAddress_US_M','OpinionLabKeys_editAddress');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_editAddressp_Baby_M','OpinionLabKeys_editAddress');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_editAddress_CA_M','OpinionLabKeys_editAddress');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_findOnMap_US_M','OpinionLabKeys_findOnMap');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_findOnMap_Baby_M','OpinionLabKeys_findOnMap');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_findOnMap_CA_M','OpinionLabKeys_findOnMap');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_galleryPage_US_M','OpinionLabKeys_galleryPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_galleryPage_BBB_M','OpinionLabKeys_galleryPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_galleryPage_CA_M','OpinionLabKeys_galleryPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_home_US_M','OpinionLabKeys_home');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_home_BBB_M','OpinionLabKeys_home');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_home_CA_M','OpinionLabKeys_home');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_loginPage_US_M','OpinionLabKeys_loginPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_loginPage_BBB_M','OpinionLabKeys_loginPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_loginPage_CA_M','OpinionLabKeys_loginPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_myAccount_US_M','OpinionLabKeys_myAccount');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_myAccount_BBB_M','OpinionLabKeys_myAccount');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_myAccount_CA_M','OpinionLabKeys_myAccount');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_myAccountCouponsPage_US_M','OpinionLabKeys_myAccountCouponsPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_myAccountCouponsPage_BBB_M','OpinionLabKeys_myAccountCouponsPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_myAccountCouponsPage_CA_M','OpinionLabKeys_myAccountCouponsPage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_myregistries_US_M','OpinionLabKeys_myregistries');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_myregistriesn_BBB_M','OpinionLabKeys_myregistries');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_myregistries_CA_M','OpinionLabKeys_myregistries');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_newUserCreation_US_M','OpinionLabKeys_newUserCreation');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_newUserCreation_BBB_M','OpinionLabKeys_newUserCreation');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_newUserCreation_CA_M','OpinionLabKeys_newUserCreation');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_orderConfirmation_US_M','OpinionLabKeys_orderConfirmation');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_orderConfirmation_CA_M','OpinionLabKeys_orderConfirmation');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_orderConfirmation_BBB_M','OpinionLabKeys_orderConfirmation');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_orderDetail_US_M','OpinionLabKeys_orderDetail');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_orderDetail_BBB_M','OpinionLabKeys_orderDetail');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_orderDetail_CA_M','OpinionLabKeys_orderDetail');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_orderHistory_US_M','OpinionLabKeys_orderHistory');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_orderHistory_BBB_M','OpinionLabKeys_orderHistory');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_orderHistory_CA_M','OpinionLabKeys_orderHistory');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_performSearch_US_M','OpinionLabKeys_performSearch');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_performSearch_BBB_M','OpinionLabKeys_performSearch');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_performSearch_CA_M','OpinionLabKeys_performSearch');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_pickInStore_US_M','OpinionLabKeys_pickInStore');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_pickInStore_BBB_M','OpinionLabKeys_pickInStore');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_pickInStore_CA_M','OpinionLabKeys_pickInStore');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_previewItems_US_M','OpinionLabKeys_previewItems');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_previewItems_BBB_M','OpinionLabKeys_previewItems');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_previewItemsy_CA_M','OpinionLabKeys_previewItems');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_privacy_US_M','OpinionLabKeys_privacy');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_privacy_BBB_M','OpinionLabKeys_privacy');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_privacy_CA_M','OpinionLabKeys_privacy');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_productpage_US_M','OpinionLabKeys_productpage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_productpage_BBB_M','OpinionLabKeys_productpage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_productpage_CA_M','OpinionLabKeys_productpage');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registrantView_US_M','OpinionLabKeys_registrantView');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registrantView_BBB_M','OpinionLabKeys_registrantView');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registrantView_CA_M','OpinionLabKeys_registrantView');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryComplete_US_M','OpinionLabKeys_registryComplete');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryComplete_BBB_M','OpinionLabKeys_registryComplete');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryComplete_CA_M','OpinionLabKeys_registryComplete');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryGifterView_US_M','OpinionLabKeys_registryGifterView');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryGifterView_BBB_M','OpinionLabKeys_registryGifterView');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryGifterView_CA_M','OpinionLabKeys_registryGifterView');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryLanding_US_M','OpinionLabKeys_registryLanding');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryLanding_BBB_M','OpinionLabKeys_registryLanding');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryLanding_CA_M','OpinionLabKeys_registryLanding');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registrySearchResult_US_M','OpinionLabKeys_registrySearchResult');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registrySearchResult_BBB_M','OpinionLabKeys_registrySearchResult');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registrySearchResult_CA_M','OpinionLabKeys_registrySearchResult');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryShippingAddressGuest_US_M','OpinionLabKeys_registryShippingAddressGuest');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryShippingAddressGuest_BBB_M','OpinionLabKeys_registryShippingAddressGuest');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryShippingAddressGuest_CA_M','OpinionLabKeys_registryShippingAddressGuest');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryShippingAddressRegisteredUser_US_M','OpinionLabKeys_registryShippingAddressRegisteredUser');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryShippingAddressRegisteredUser_BBB_M','OpinionLabKeys_registryShippingAddressRegisteredUser');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_registryShippingAddressRegisteredUser_CA_M','OpinionLabKeys_registryShippingAddressRegisteredUser');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_selfHeader_US_M','OpinionLabKeys_selfHeader');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_selfHeader_BBB_M','OpinionLabKeys_selfHeader');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_selfHeader_CA_M','OpinionLabKeys_selfHeader');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shippingAddressGuest_BBB_M','OpinionLabKeys_shippingAddressGuest');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shippingAddressGuest_US_M','OpinionLabKeys_shippingAddressGuest');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shippingAddressGuest_CA_M','OpinionLabKeys_shippingAddressGuest');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shippingAddressRegisteredUser_US_M','OpinionLabKeys_shippingAddressRegisteredUser');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shippingAddressRegisteredUser_BBB_M','OpinionLabKeys_shippingAddressRegisteredUser');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shippingAddressRegisteredUser_CA_M','OpinionLabKeys_shippingAddressRegisteredUser');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shippingTable_US_M','OpinionLabKeys_shippingTable');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shippingTable_BBB_M','OpinionLabKeys_shippingTable');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shippingTable_CA_M','OpinionLabKeys_shippingTable');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shoppingCart_US_M','OpinionLabKeys_shoppingCart');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shoppingCart_BBB_M','OpinionLabKeys_shoppingCart');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_shoppingCart_CA_M','OpinionLabKeys_shoppingCart');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_storeDetails_US_M','OpinionLabKeys_storeDetails');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_storeDetails_BBB_M','OpinionLabKeys_storeDetails');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_storeDetailsr_CA_M','OpinionLabKeys_storeDetails');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_storeLocator_US_M','OpinionLabKeys_storeLocator');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_storeLocator_BBB_M','OpinionLabKeys_storeLocator');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_storeLocator_CA_M','OpinionLabKeys_storeLocator');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_updateCreditCard_US_M','OpinionLabKeys_updateCreditCard');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_updateCreditCard_BBB_M','OpinionLabKeys_updateCreditCard');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_updateCreditCard_CA_M','OpinionLabKeys_updateCreditCard');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_viewCreditCard_US_M','OpinionLabKeys_viewCreditCard');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_viewCreditCard_BBB_M','OpinionLabKeys_viewCreditCard');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('OpinionLabKeys_viewCreditCard_CA_M','OpinionLabKeys_viewCreditCard');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('SinglePageCheckoutOn_US_M','800002_SinglePageCheckoutOn');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('SinglePageCheckoutOn_Baby_M','800002_SinglePageCheckoutOn');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('SinglePageCheckoutOn_CA_M','800002_SinglePageCheckoutOn');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('CertonaKeys_HPFunNewProdMax_BBC_M','DC11100008HPFunNewProdMax');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('CertonaKeys_HPFunNewProdMax_BBU_M','DC11100008HPFunNewProdMax');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('CertonaKeys_HPFunNewProdMax_BBB_M','DC11100008HPFunNewProdMax');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('CertonaKeys_PDPCustAlsoViewProdMax_BBC_M','DC11100008PDPCustAlsoViewProdMax');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('CertonaKeys_PDPCustAlsoViewProdMax_BBU_M','DC11100008PDPCustAlsoViewProdMax');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('CertonaKeys_PDPCustAlsoViewProdMax_BBB_M','DC11100008PDPCustAlsoViewProdMax');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('CertonaKeys_PDPOOSMax_BBC_M','BBBPSI6CKS10001');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('CertonaKeys_PDPOOSMax_BBU_M','BBBPSI6CKS10001');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('CertonaKeys_PDPOOSMax_BBB_M','BBBPSI6CKS10001');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('DefaultStoreId_TBS_Canada','DC11200006DefaultStoreId');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('DefaultStoreId_TBS_US','DC11200006DefaultStoreId');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('DefaultStoreId_TBS_Baby','DC11200006DefaultStoreId');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_enableOPBBoosting_DSK_US','FlagDrivenFunctions_enableOPBBoosting');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_enableOPBBoosting_DSK_BAB','FlagDrivenFunctions_enableOPBBoosting');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_enableOPBBoosting_DSK_CA','FlagDrivenFunctions_enableOPBBoosting');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_enableOPBBoosting_MOB_US','FlagDrivenFunctions_enableOPBBoosting');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_enableOPBBoosting_MOB_BAB','FlagDrivenFunctions_enableOPBBoosting');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('FlagDrivenFunctions_enableOPBBoosting_MOB_CA','FlagDrivenFunctions_enableOPBBoosting');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('MobileWebConfig_requestDomainName_US_MA','PUB1400001');
Insert into BBB_CORE_PRV.BBB_CONFIG_SITE_TRANSLATN (TRANSLATION_ID,CONFIG_KEY_ID) values ('MobileWebConfig_requestDomainName_BBB_MA','PUB1400001');

--- insert BBB_CORE_PRV.BBB_CONFIG_KEYS

Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS (CONFIG_KEY_ID,CONFIG_TYPE,CONFIG_TYPE_DESC,CONFIG_TYPE_ID) values ('invLocalStoreRepoSchedulerConfigValues','80112','invLocalStoreRepoSchedulerConfigValues','invLocalStoreRepoSchedulerConfigValues');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS (CONFIG_KEY_ID,CONFIG_TYPE,CONFIG_TYPE_DESC,CONFIG_TYPE_ID) values ('NotifyRegistrant','80110','NotifyRegistrant','NotifyRegistrant');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS (CONFIG_KEY_ID,CONFIG_TYPE,CONFIG_TYPE_DESC,CONFIG_TYPE_ID) values ('OmnitureBoosting','80111','OmnitureBoosting','OmnitureBoosting');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS (CONFIG_KEY_ID,CONFIG_TYPE,CONFIG_TYPE_DESC,CONFIG_TYPE_ID) values ('OpinionLabKeys','80106','OpinionLabKeys','OpinionLabKeys');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS (CONFIG_KEY_ID,CONFIG_TYPE,CONFIG_TYPE_DESC,CONFIG_TYPE_ID) values ('BBBAOI99999','80115','AdvancedOrderInquiryKeys','BBBAOI99999');
Insert into BBB_CORE_PRV.BBB_CONFIG_KEYS (CONFIG_KEY_ID,CONFIG_TYPE,CONFIG_TYPE_DESC,CONFIG_TYPE_ID) values ('EDWfields','80113','EDWfields','EDWfields');

--- insert BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE

Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11100008','CertonaKeys_certona_quick_view_scheme');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11100008','CertonaKeys_certona_search_pdt_count');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11100008','CertonaKeys_certona_search_scheme');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11100008','CertonaKeys_NOSEARCHMax');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('500001','ContentCatalogKeys_address_country_code');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('500001','ContentCatalogKeys_mobileSmartSEOOn');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('500001','ContentCatalogKeys_scene7EndecaImageSize');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('500001','ContentCatalogKey_bigBlueToken');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('500001','ContentCatalogKey_order_confirm_mcid_param_email');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('500001','ContentCatalogKeys_FindAStoreOn');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('500001','ContentCatalogKeys_ResponsysCookieName');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('500001','ContentCatalogKeys_maxEDWRepoRetry');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('500001','ContentCatalogKeys_EDW_TTL');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('800002','FlagDrivenFunctions_challenge_question_flag');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('800002','FlagDrivenFunctions_google_address_switch');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('800002','FlagDrivenFunctions_LOCAL_STORE_PDP_FLAG');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('800002','FlagDrivenFunctions_Narvar_TrackingFlag');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('800002','FlagDrivenFunctions_notifyRegistrantFlag');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('800002','FlagDrivenFunctions_SUPPLY_BALANCE_ON');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('800002','FlagDrivenFunctions_ResponsysEnabled');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('800002','FlagDrivenFunctions_hoorayModalOn');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('800002','FlagDrivenFunctions_enableOPBBoosting');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('800002','FlagDrivenFunctions_registryOwnerViewShowTopPromoSlots');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('800002','FlagDrivenFunctions_enableUGCQV');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('800002','FlagDrivenFunctions_ship_promo_banner_flag_pdp');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('800002','FlagDrivenFunctions_ship_promo_banner_flag_cart');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11000016','HTTPClientOptParams_BedBathCanada_certona_quickview');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11000016','HTTPClientOptParams_BedBathCanada_certona_search_rr');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11000015','HTTPClientOptParams_BedBathUS_certona_quickview');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11000015','HTTPClientOptParams_BedBathUS_certona_search_rr');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11000014','HTTPClientOptParams_BuyBuyBaby_certona_quickview');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11000014',' HTTPClientOptParams_BuyBuyBaby_certona_search_rr');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11000004','HTTPClientReqParams_BedBathCanada_certona_quickview');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11000004','HTTPClientReqParams_BedBathCanada_certona_search_rr');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11000003','HTTPClientReqParams_BedBathUS_certona_quickview');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11000003','HTTPClientReqParams_BedBathUS_certona_search_rr');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11000002','HTTPClientReqParams_BuyBuyBaby_certona_quickview');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11000002','HTTPClientReqParams_BuyBuyBaby_certona_search_rr');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('300002','ThirdPartyURLs_CookieLatLng');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('300002','ThirdPartyURLs_mapQuestSearchStringForLatLng');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('300002','ThirdPartyURLS_Narvar_Track_Num_Param');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('300002','ThirdPartyURLS_Narvar_TrackingUrl');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('300002','ThirdPartyURLs_LocalStoreRepoFetch');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('300002','ThirdPartyURLs_OracleResponsys_url');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('300002','ThirdPartyURLs_mxFooter_emailSignUpLink');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegisrty_BBBWebServiceLogin_createRegistry2');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegisrty_BBBWebServiceLogin_getProfile');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegisrty_BBBWebServiceLogin_getRegistryInfo2');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegisrty_BBBWebServiceLogin_updateRegistry2');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegisrty_BBBWebServicePwd_createRegistry2');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegisrty_BBBWebServicePwd_getProfile');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegisrty_BBBWebServicePwd_getRegistryInfo2');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegisrty_BBBWebServicePwd_updateRegistry2');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegisrty_SFL_MAX_LIMIT');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegisrty_WSEndPoint_createRegistry2');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegisrty_WSEndPoint_getRegistryInfo2');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegisrty_WSEndPoint_updateRegistry2');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegistry_WSEndPoint_getProfile');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11900001','WSDL_GiftRegisrty_CART_MAX_LIMIT');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('600003','WSStubClasses_createRegistry2');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('600003','WSStubClasses_getProfile');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('600003','WSStubClasses_getRegistryInfo2');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('600003','WSStubClasses_updateRegistry2');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_DC3ROWXNGUSER');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_ROWXNGUSER');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_STG1ROWXNGUSER');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_STG2ROWXNGUSER');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_STG3ROWXNGUSER');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_DC1ROWXNGUSER');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_DC2ROWXNGUSER');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_LogErrors');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_ShowDetailedErrorInResponse');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_BbbyValidRegTypes');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_BabyValidRegTypes');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_CAValidRegTypes');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_PROCESS_CD');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_minSkuLength');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_maxSkuLength');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_STATE_ABBREVATION');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_STATE_ABBREVATION_CA');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_LastMaintProgram');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11200006','GiftRegistryConfig_DefaultRegistryType');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC11400001','CartAndCheckoutKeys_AdditionShipping_StateCodesList');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DIM12301','DimDisplayConfig_Ratings');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC1500003','DimNonDisplayConfig_Eligible_Customizations');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('BBBR221CKS001','International_Shipping_po_decrypt_pgp_file_path');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('200002','ValueLinkKeys_MwkKeys');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC99000004','DefaultStoreType_radius_fis_pdp');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('invLocalStoreRepoSchedulerConfigValues','invLocalStoreRepoSchedulerConfigValues_RecipientFrom');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('invLocalStoreRepoSchedulerConfigValues','invLocalStoreRepoSchedulerConfigValues_RecipientTo');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('invLocalStoreRepoSchedulerConfigValues','invLocalStoreRepoSchedulerConfigValues_PoolSize');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('NotifyRegistrant','NotifyRegistrant_daysOffthreshold');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('NotifyRegistrant','NotifyRegistrant_global_InventoryThreshold');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_OmnitureAPISecretKey');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_OmnitureAPIUserName');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_OmnitureEndPointURL');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_OmnitureReportDaysFrom');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_OmnitureReportDaysTo');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_OmnitureReportGet');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_OmnitureReportMetricId');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_OmnitureReportQueue');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_OmnitureReportRetentionDays');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_OmnitureTopSearchedTerms');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_OmnitureTotalSearchedTerms');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_OmnitureReportSuiteId');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_RecipientTo');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OmnitureBoosting','OmnitureBoosting_RecipientFrom');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_college');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_findCollege');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_gifts');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_circular');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_shippingInfo');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_privacyPolicy');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_viewCreditCard');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_updateCreditCard');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_storeLocator');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_topconsultant');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_storeDetails');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_shoppingCart');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_shippingTable');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_shippingAddressRegisteredUser');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_shippingAddressGuest');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_registryShippingAddressRegisteredUser');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_selfHeader');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_searchResult');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_registryLanding');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_registryComplete');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_registryGifterView');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_registrantView');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_productpage');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_privacy');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_previewItems');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_pickInStore');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_performSearch');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_orderHistory');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_orderDetail');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_myAccount');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_newUserCreation');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_myregistries');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_myAccountCouponsPage');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_orderConfirmation');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_loginPage');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_kickstarters');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_internationalShippingInfo');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_home');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_galleryPage');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_findOnMap');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_editAddress');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_easyReturns');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_directionOnMap');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_createUserRegisteration');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_createRegistry');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_createAccountAfter');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_contactUs');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_checkoutSignin');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_checkoutShippingPage');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_categorylanding');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_categoryNavigation');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_cartCouponsPage');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_brandlisting');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_brandResult');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_billingInfoTop');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_billingInfoAddressRegisteredUser');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_billingInfo');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_addressBook');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_addCreditCard');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_OpinionLabGlobalKey');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_errorPage');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_productDetail');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_orderConfirmationIS');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_checkout');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_wishlist');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_registrySearchResult');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('OpinionLabKeys','OpinionLabKeys_registryShippingAddressGuest');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC12000001','ServiceCacheMapType_getLeftNavigation');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC12000001','ServiceCacheMapType_getRestStoreDetails');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC12000001','ServiceCacheMapType_getAllLabelForMobile');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('DC12000001','ServiceCacheMapType_getRestStoreDetailsVO');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('BBBAOI99999','BBBAOIRS99999');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('BBBAOI99999','BBBAOIPP99999');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('BBBAOI99999','BBBAOIDR99999');
Insert into BBB_CORE_PRV.BBB_REL_CONFIG_KEY_VALUE (CONFIG_TYPE_ID,CONFIG_KEY_ID) values ('EDWfields','EDWfields_is_tibco_enabled');

--- update label value : BBB_CORE_PRV.BBB_LBL_TXT

UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Reset Password?' WHERE LBL_TXT_ID = 'mobweb200415';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Reset&nbsp;Password?' WHERE LBL_TXT_ID = '600079';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Reset Your Password?' WHERE LBL_TXT_ID = '600783';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Cart' WHERE LBL_TXT_ID = 'mobweb200343';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'My Bed Bath' WHERE LBL_TXT_ID = 'DC1bbPSI5000041';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'SIGN IN' WHERE LBL_TXT_ID = 'mobweb200195';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Password Reset' WHERE LBL_TXT_ID = 'mobweb200445';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Estimated Shipping' WHERE LBL_TXT_ID = 'mobweb200351';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Bill Me Later' WHERE LBL_TXT_ID = 'BBBR2100000070';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Create another Registry' WHERE LBL_TXT_ID = '600761';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Manage your Registry' WHERE LBL_TXT_ID = '601095';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Welcome,' WHERE LBL_TXT_ID = 'DC2bbLTA1630002';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'registry id:' WHERE LBL_TXT_ID = 'DC1bbMARPS10007';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Purchased' WHERE LBL_TXT_ID = 'DC2bbLTA1240008';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = '*' WHERE LBL_TXT_ID = '600802';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Email' WHERE LBL_TXT_ID = '600081';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Reset Your Password' WHERE LBL_TXT_ID = '600784';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Forgot your Password?' WHERE LBL_TXT_ID = '600694';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'items' WHERE LBL_TXT_ID = 'mobweb200350';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Select State' WHERE LBL_TXT_ID = '600786';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = '< My Registries' WHERE LBL_TXT_ID = '600562';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'MY ITEMS' WHERE LBL_TXT_ID = 'DC1bbLTA1260001';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'shop this look' WHERE LBL_TXT_ID = 'BBBR2100000166';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'your favorite store' WHERE LBL_TXT_ID = '800611';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'miles' WHERE LBL_TXT_ID = '600209';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'State' WHERE LBL_TXT_ID = '600182';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'College' WHERE LBL_TXT_ID = '601204';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Fee applied' WHERE LBL_TXT_ID = 'BBBPSI7LTA10184';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'No Fee Applied' WHERE LBL_TXT_ID = 'BBBPSI7LTA10182';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Fee applied' WHERE LBL_TXT_ID = 'BBBPSI7LTA10187';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'No Fee Applied' WHERE LBL_TXT_ID = 'BBBPSI7LTA10185';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Requested' WHERE LBL_TXT_ID = 'BBBPSI7LTA10651';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Order Subtotal with Offer(s)' WHERE LBL_TXT_ID = 'BBBPSI7LTA10204';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Added' WHERE LBL_TXT_ID = 'DC2bbLTA1240007';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Fee applied' WHERE LBL_TXT_ID = 'BBBPSI7LTA10231';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'No Fee Applied' WHERE LBL_TXT_ID = 'BBBPSI7LTA10229';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Ask a Friend' WHERE LBL_TXT_ID = 'BBBPSI6LTA10104';

UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Checkout' WHERE LBL_TXT_ID = 'mobweb200483';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Add your complimentary gift message.' WHERE LBL_TXT_ID = 'mobweb200185';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Shipping Information' WHERE LBL_TXT_ID = 'mobweb200148';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Credit Card' WHERE LBL_TXT_ID = 'mobweb200225';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Order Subtotal' WHERE LBL_TXT_ID = 'BBBRLTLLTA2033';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Total' WHERE LBL_TXT_ID = 'mobweb200550';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Select your delivery service level' WHERE LBL_TXT_ID = 'BBBRLTLLTA1023';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Select your delivery service level' WHERE LBL_TXT_ID = 'BBBRLTLLTA2018';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Additional service charges may apply' WHERE LBL_TXT_ID = 'BBBPSI7LTA10082';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Additional service charges may apply' WHERE LBL_TXT_ID = 'BBBPSI7LTA10090';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Go to Quick Picks' WHERE LBL_TXT_ID = 'BBBR2100000296';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Registry Quick Picks!' WHERE LBL_TXT_ID = 'BBBR2100000293';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'quick picks' WHERE LBL_TXT_ID = 'DC2bbLTA1020005';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'quick picks' WHERE LBL_TXT_ID = 'DC1bbLTA1260002';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Quick Picks' WHERE LBL_TXT_ID = 'DC1bbLTA2710018';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET LABEL_VALUE = 'Refine' WHERE LBL_TXT_ID = 'mobweb200007';


--- update text area : BBB_CORE_PRV.BBB_LBL_TXT

UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET TEXTAREA_VALUE = '<style>
#shopForCollegelFlyout .scheduleAppointmentCall .button_secondary {margin-left:0px;}
#shopForCollegelFlyout .scheduleAppointmentCall .button_secondary:hover {margin-left:0px;}
</style>
<div id=""collegeRegistryFlyoutChecklist"" class=""marBottom_20""> 
     <h2><a href=""/store/registry/CollegeChecklistPage"" title=""College Checklist"">College<br/>Checklist</a></h2> 
</div>
<h2>Appointment Scheduling</h2>
<p class=""marBottom_20"">Make an appointment with one of our college experts to get campus ready.</p>
<div class=""scheduleAppointmentCall"" data-param-appointmentcode=""COL"" data-param-storeid="""" data-param-registryId="""" data-param-coregFN="""" data-param-coregLN="""" data-param-eventDate=""""></div>
' WHERE LBL_TXT_ID = '700076';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET TEXTAREA_VALUE = '<div class=""startBrowse"" style=""background-image: url(/_assets/mobileAssets/global/images/startBrowse.png); background-repeat: no-repeat; background-size: 100% 100%; height:150px; width:100%; margin:0px;"">
                <input type=""submit"" class="" btnRegistryPrimary startBrowseRound"" value=""START BROWSING TEST"" style=""margin-right: 10%; float: right; margin-top: 92px; padding:0.5% 2% 0% 2%;font-size:12px;border-radius: 0;width:40%"" onclick=""omniture.startBrowsingClick()""; >
                <input type=""hidden"" name=""startBrowsingSuccessURL"" id=""startBrowsingSuccessURL"" value=""cart""></div>' WHERE LBL_TXT_ID = 'BBBPSI7LTA10089';

UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET TEXTAREA_VALUE = '<div class="pickUpInStoreMsgBOPUS">                <h3>In-Store Pickup - Reserve Online Pay in Store.</h3> <a href="#" class="BopusMsgMore" aria-hidden=''false''>Learn More</a><a href="#" class="BopusMsgLess hidden" aria-hidden=''true''>Hide</a>               <div class="BopusMsg padTop_10 hidden">                                                               <p>Excludes AK, HI, Puerto Rico and International Orders</p>
                                <ol>
                                                <li>1. Select "Find In Store" to find out pick up availability. Select your location and "Add to Cart".</li>
                                                <li>2. Go to Cart and check out with your credit card information to <strong>Reserve</strong> your order.</li>
                                                <li>3. Your "Ready for Pick Up" email will arrive within 3 hours. Present this email and <strong>Pay</strong> for your item(s) at pick up.</li>
<li>4. Items will be held for 2 days after you receive your "Ready for Pick Up" email.</li>
                                </ol> 
<hr>
<p><strong>Going off to College and want your items held longer? Learn about Pack & Hold and other solutions to start your journey. <a href="/store/registry/MovingSolution">View Details</a></strong></p>
<p><strong>Any items reserved online from a registry will not show as purchased until they are picked up and paid for at the store.</strong></p>
</div>                            
</div>                            ' WHERE LBL_TXT_ID = 'DC15500002';
				
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET TEXTAREA_VALUE = '<p>Please check the email associated with your Bed, Bath &amp; Beyond account.</p>
<p>We sent you a secure link to reset your password.</p>' WHERE LBL_TXT_ID = '700103';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET TEXTAREA_VALUE = 'Blank_value' WHERE LBL_TXT_ID = 'BBBR221LTA0011';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET TEXTAREA_VALUE = 'Please enter the email address associated with your Bed Bath & Beyond account to receive a reset password link.' WHERE LBL_TXT_ID = '700102';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET TEXTAREA_VALUE = '<button type=""button"" class=""close"" data-dismiss=""modal""  aria-label=""close"" >&times;</button>
	<img src=""/_assets/mobileAssets/global/images/verifiedByVisa.gif"" alt=""verified by visa"">
	<h2>Protect Your Visa Card</h2>
	<div>Verified by Visa is free to Visa cardholders and was developed to help prevent unauthorized use of Visa cards online.</div>
	<div>Verified by Visa protects Visa cards with personal passwords, giving cardholders reassurance that only they can use their Visa cards online.</div>
	<div>Once your card is activated, your card number will be recognized whenever it''s used at participating online stores. A Verified by Visa window will automatically appear and your Visa card issuer will ask for your password. You''ll enter your password to verify your identity and complete your purchase.
	At stores that aren''t participating in Verified by Visa yet, your Visa card will continue to work as usual. You can also 
	<a href=""https://verified.visa.com/aam/activation/landingPage.aam"" target=""_blank"">activate your current card now.</a></div>
	<div class=""txtSmall"">
		<img src=""/_assets/mobileAssets/global/images/visa.gif"" alt=""visa"">&copy; Copyright 2015, Visa U.S.A. All rights reserved.
	</div>' WHERE LBL_TXT_ID = 'BBBR2100000115';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET TEXTAREA_VALUE = '<button type=""button"" class=""close"" data-dismiss=""modal""  aria-label=""close"">&times;</button>
	<h2>MasterCard Secure Code</h2>
	<div>bedbathandbeyond.com supports MasterCard SecureCode, a security technology that authenticates your MasterCard card and ensures that only the authorized cardholder is placing the order. If the bank that issued your MasterCard supports SecureCode, you may be prompted to enter your password or to sign up for MasterCard SecureCode as you check out. You can also <a href=""http://www.mastercardsecurecode.com"" target=""_blank"">activate your current card now.</a></div>
	<div class=""txtSmall"">
		<img src=""/_assets/mobileAssets/global/images/master.gif"" alt=""master card"">&copy; Copyright 2015, Mastercard U.S.A. All rights reserved.
	</div>' WHERE LBL_TXT_ID = 'BBBR2100000116';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET TEXTAREA_VALUE = '<div class="marPromoBox_LR regLandPromoBoxBorder"> <a href="/m/kickStarters?icid=homepage_Promoarea1-homepage"> <img src="/_assets/mobileAssets/global/images/bbb_registry_kickstarter.png" alt="Registry Kickstarter"> </a></div>' WHERE LBL_TXT_ID = 'BBBPSI7LTA10137';

--- update error message : BBB_CORE_PRV.BBB_LBL_TXT

UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET ERROR_MSG = 'The selected shipping method is invalid for one of the products. Please edit the shipping method to proceed.' WHERE LBL_TXT_ID = '1500003';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET ERROR_MSG = 'The selected shipping method is invalid for one of the products. Please edit the shipping method to proceed.' WHERE LBL_TXT_ID = 'MobErrorMsg50455';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET ERROR_MSG = 'Email address must be less than 30 characters.' WHERE LBL_TXT_ID = '800155';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET ERROR_MSG = 'Email address must be less than 50 characters.' WHERE LBL_TXT_ID = 'MobErrorMsg50147';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT SET ERROR_MSG = 'Passwords must be 8-20 characters in length. Must contain at least 1 uppercase' WHERE LBL_TXT_ID = 'MobErrorMsg50402';

--- insert new labels : BBB_CORE_PRV.BBB_LBL_TXT

INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10002','lbl_find_registry_nonlogged','1','0','Find A Registry','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10003','lbl_already_registered','1','0','Already Registered?','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10004','lbl_confirm_email','1','0','Email','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10005','lbl_confirm_password','1','0','Confirm Password','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10006','lbl_confirm_password_modal_change','1','0','Confirm Password','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10007','lbl_confirm_password_field','1','0','confirm password','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10008','lbl_reset_password_submit','1','0','SUBMIT','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10009','lbl_reset_password_header','1','0','Reset Password','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10012','lbl_registry_id','1','0','Registry ID:','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10013','lbl_subscribe_email_billing','1','0','Yes, subscribe to email offers. First-time subscribers get a 20% off one single item email offer for in-store use.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10014','lbl_reset_password_message_content','1','0','Your password has been reset, please login using new password','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10015','lbl_reset_password_message_head','1','0','You successfully changed your password. Log In below.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10017','lbl_select_registry_type','1','0','Select the type of registry you would like to create','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10351','lbl_recom_date_sort','1','0','Newest to Oldest','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10019','lbl_reset_password_message_head','1','0','You successfully changed your password. Log In below.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10021','lbl_mob_search_clear_all','1','0','Clear All','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10022','lbl_dsl_alt_modal_head','1','0','For truck delivery items to be added to your registry, an alternate phone number must be provided.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10023','lbl_alternate_no','1','0','Alternate Phone','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10025','lbl_forgot_password_heading2','1','0','Forgot Your Password?','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10028','lbl_update_registry','1','0','Update Registry','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10029','lbl_update_service_level','1','0','Update service level','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10030','lbl_dsl_updated_successfully','1','0','The service level has been updated successfully.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10031','lbl_truck_delivery_opt','1','0','Truck Delivery Option','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10033','lbl_sfl_remove_item','1','0','Are you sure you want to remove this item?','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10034','lbl_my_offers_no_offers_msg','1','0','No Offers are available at this time','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10035','lbl_my_offers_heading','1','0','My Offers','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10036','lbl_cart_sign_in_msg','1','0','Sign In','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10037','lbl_cart_sign_in_button','1','0','SIGN IN','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10039','lbl_sfl_remove_popup','1','0','Remove item from list?','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10041','lbl_update_service_level_mob','1','0','Update service level.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10042','lbl_select_delivery_valid','1','0','Please select delivery option.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10352','lbl_update_reg','1','0','Update Registry','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10043','lbl_holiday_messaging','1','0','Holiday Shipping Details','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10044','lbl_vendor_shipping','1','0','Vendor Shipping Details','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10045','lbl_offerdetails_and_exlusions','1','0','Details & Restrictions','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10046','lbl_excluded_shop_now_ship_later','1','0','Excluded for Shop Now, Ship Later','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10047','lbl_offers_applied','1','0','Offer(S) Applied','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10048','lbl_upc','1','0','UPC','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10049','lbl_size','1','0','SIZE','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10050','lbl_item_price','1','0','Item Price','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10051','lbl_inclusive','1','0','Incl','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10052','lbl_delivery_service_level','1','0','Delivery Service Level','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10053','lbl_color','1','0','COLOR','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10057','lbl_higher_free_shipping_threshold','1','0','25','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10058','lbl_lower_free_shipping_threshold','1','0','17','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10060','coupons_update_link','1','0','Update Location','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10061','lbl_coupon_tooltip_signIn_link','1','0','SIGN IN/ACCOUNT','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10062','lbl_coupon_update_location_msg','1','0','All Offers do Not apply for international Shipping. Please change shipping inforamation to USA to apply offers.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10063','lbl_coupons_tooltip_description','1','0','You have Offers. Please click to view.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10064','lbl_coupons_tooltip_note_msg','1','0','Note: Offers will not be accessible after expiration','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10065','lbl_coupon_intl_error_msg','1','0','No offers and coupons available for International Users.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10067','lbl_view_offers','1','0','VIEW OFFERS','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10068','lbl_checkout_my_offers','1','0','My Offers','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10069','lbl_checkout_access_offers','1','0','To access offers, please enter email and phone number ','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10070','lbl_global_price_changes','1','0','Cart has items whose price has changed. Please scroll down for details.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10071','lbl_global_price_update','1','0','Price updates','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10072','lbl_global_now','1','0','Now','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10073','lbl_cart_intl_restriction_msg','1','0','A Product in your cart does not ship Internationally','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10074','lbl_cart_oos_global','1','0','Items in the cart are Out of Stock Online.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10075','lbl_global_remove','1','0','Remove','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10076','lbl_cart_intl_restricted_msg','1','0','Restricted from Shipping Internationally','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('DC1bbLTA2720002','lbl_alternate_number','1','0','Please enter an alternate number','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10088','lbl_pricing_details_with_offers','1','0','Pricing Details With Offers Applied','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10090','lbl_include_gift_packaging_for','1','0','Include Gift packaging for','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10091','lbl_ship_now_ship_later_msg','1','0','Shop Now, Ship Later. Order now and have your items delivered at a specific date.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10092','lbl_ship_now_ship_later_learn_more','1','0','Learn More','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10093','lbl_add_common_greeting','1','0','Add Common Greeting','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10094','lbl_billing_gift_packaging','1','0','The packaging slip included with your gift order will not display the price.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10095','lbl_spc_subscribe_email_billing','1','0','Yes, subscribe to email offers. First-time subscribers get a 20% off one single item email offer for in-store use','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10096','lbl_billing_save_to_myaccount','1','0','Save to my account','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10097','lbl_billing_full_name','1','0','Full Name','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10101','lbl_apply','1','0','Apply','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10102','lbl_select_greeting','1','0','Select a Greeting','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10084','lbl_mycart_continue_shopping','1','0','CONTINUE SHOPPING','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10103','lbl_truck_delivery','1','0','Truck delivery','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10104','lbl_sfl_oos_global','1','0','Items in the Saved Items are Out of Stock Online.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10105','lbl_pdp_product_add_cart','1','0','ADD TO CART','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10106','lbl_pdp_product_add_registry','1','0','ADD TO REGISTRY','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10107','lbl_pdp_product_added_cart','1','0','ADDED TO CART','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10108','lbl_pdp_product_added_registry','1','0','ADDED TO REGISTRY','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10109','lbl_add_another_gift_card','1','0','Add another gift card or tap "<" to go back to all Payments','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10110','lbl_amount_due','1','0','Amount Due','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10111','lbl_back','1','0','Back','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10112','lbl_covered_by_gift_card','1','0','Covered by Gift Card:','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10113','lbl_enter_gift_card','1','0','Enter Gift Card','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10114','lbl_gift_card_balance','1','0','Gift Card Balance:','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10115','lbl_gift_card_edit','1','0','Edit','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10116','lbl_gift_card_ending_in','1','0','Gift Card Ending in','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10117','lbl_gift_card_pin','1','0','Gift Card Pin','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10118','lbl_back_all_payment','1','0','Back to All Payment','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10119','lbl_payment_title','1','0','Payment','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10120','lbl_spc_gc_ccgc','1','0','You can combine payment using gift cards, merchandise credit cards and a credit card.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10121','lbl_ss_mob_full_name','1','0','Full Name (First Name, Last Name)','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10122','lbl_ss_mob_ship_addr','1','0','Shipping Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10123','lbl_ss_mob_addition_ship_charge','1','0','Additional charge will be added when shipping to HI, AL & PR.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10124','lbl_ss_mob_view_all','1','0','View All','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10125','lbl_ss_mob_view_ship_cost','1','0','View Shipping Costs','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10126','lbl_ss_mob_ship_edit','1','0','Edit','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10127','lbl_ss_mob_shipping_addr','1','0','Shipping Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10128','lbl_ss_use_as_billing_add','1','0','Use as my billing address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10130','lbl_how_to_find_pin','1','0','How to find your pin','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10131','challenge_question_success_message','1','0','Challenge Question setup completed successfully.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10132','lbl_billing_address_billing','1','0','Billing','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10133','lbl_billing_your_contact_info','1','0','Your Contact Info','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10134','lbl_spc_address_addNewAdd_button','1','0','Add a New Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10135','lbl_spc_address_edit_button','1','0','Edit','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10136','lbl_spc_address_viewAll_button','1','0','View All','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10137','lbl_spc_apply_button','1','0','Apply','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10138','lbl_spc_cancel_button','1','0','Cancel','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10139','lbl_spc_order_review','1','0','Order Review','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10140','lbl_find_store_benefits_Item_2','1','0','Indicates stores with Health & Beauty Department.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10141','lbl_store_details_directions','1','0','DIRECTIONS','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10142','lbl_store_route_map','1','0','MAP','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10143','lbl_bopus_pickup_instore','1','0','Pick Up In Store','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10144','lbl_bopus_pickup_instore_small','1','0','(pay in store)','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10145','lbl_creditcard_listall','1','0','View All','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10147','lbl_spc_creditcard_payment','1','0','Payment','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10148','lbl_spc_creditcard_edit','1','0','Edit','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10149','lbl_spc_creditcard_add_new','1','0','Add New','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10150','lbl_spc_creditcard_view_all','1','0','View All','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10151','lbl_spc_creditcard_remove','1','0','Remove','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10152','lbl_spc_creditcard_back_to_allpayment','1','0','Back to all payment methods','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10153','lbl_spc_creditcard_back','1','0','Back','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10154','lbl_spc_creditcard_credit_card_number','1','0','Credit Card Number','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10155','lbl_spc_creditcard_expiry_date','1','0','Expires','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10156','lbl_spc_creditcard_cvv_number','1','0','CVV','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10158','lbl_spc_creditcard_apply','1','0','Apply','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10159','lbl_spc_creditcard_cancel','1','0','Cancel','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10163','lbl_spc_paypal','1','0','PayPal','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10164','lbl_spc_paypal_change_payment','1','0','Change Payment','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10184','lbl_ss_mob_spc_continue_to','1','0','Continue to','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10185','lbl_ss_mob_spc_paypal_billing_restriction_msg','1','0','To update address return to PayPal','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10186','lbl_First_question','1','0','Please select First Question','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10187','lbl_email_sent','1','0','Please check the email associated with your account.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10189','lbl_reset_password','1','0','We sent you a secure link to reset your password.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10190','lbl_second_question','1','0','Please select Second Question','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10191','lbl_setup_question_msg','1','0','Set up challenge questions for more account privacy.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10193','lbl_offers_present_message','1','0','You have offers. Please click on view offers.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10194','lbl_offers_accessibility_message','1','0','Note: Offers will not be accessible after expiration.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10195','lbl_offers_bopus_items','1','0','Offers can''t be applied to pickup items.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10196','lbl_offers_no_items','1','0','There are  no items in cart. Offers can''t be applied','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10197','lbl_giftcard_not_avilable_bopus_hybrid','1','0','Giftcard is unavailable for Reserve Online Pay in Store items. Please continue with standard Checkout.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10198','lbl_billing_info_email_placeholder','1','0','Email','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10199','lbl_billing_info_phone_placeholder','1','0','Phone Number','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10200','lbl_billingaddress_placeholder','1','0','Billing Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10201','lbl_other_recom_products','1','0','OTHER RECOMMENDED PRODUCTS','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10205','lbl_store_info','1','0','Store Info','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10206','lbl_show_password','1','0','Show password','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10207','lbl_share_account','1','0','If you choose to share your account, you will be able to login into Bed Bath & Beyond and buybuy.com','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10208','lbl_save_credit_card','1','0','Save to Account','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10209','lbl_orderconf_save_info','','0','We can save the information you have entered and create an account','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10210','lbl_hide_password','1','0','Hide password','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10211','lbl_gift_packaging','1','0','Gift Packaging:','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10212','lbl_email_orderconfirm_success','1','0','Success! Your order has been placed.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10213','lbl_email_orderconfirm_orderdate','1','0','Order Date:','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10215','lbl_contact_info','1','0','Your Contact Information','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10216','lbl_checkoutconfirmation_delivery_order','1','0','Online Order:','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10217','lbl_checkoutconfirmation_confirmation','1','0','Order Confirmation','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10218','lbl_checkoutconfirmation_bopus_msg','1','0','In Store Order :','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10219','lbl_cart_registry_from_text','1','0','FROM','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10220','lbl_bopus_store_info','1','0','Pick up In store Information','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10221','lbl_addcreditcard_preferredmailing','1','0','Make preferred mailing address','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10222','lbl_bread_crumb_mailing','1','0','Mailing','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10223','lbl_PoBox_Popup','1','0','We are unable to add this item to your registry because your shipping address contains a P.O.Box','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10224','lbl_more_orders','1','0','More Orders','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10225','lbl_item_added_to_cart','1','0','Added to Cart','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10226','lbl_spc_paypal_not_avilable_bopus_hybrid','1','0','PayPal is unavailable for Reserve Online Pay in Store items. Please continue with standard Checkout.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10227','lbl_spc_order_total','1','0','Order Total','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10232','lbl_challenge_questions','1','0','Challenge Questions','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10233','lbl_enter_question','1','0','Please enter security questions','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10234','lbl_select_question','1','0','Select a security question','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10235','lbl_security_question','1','0','Security Questions','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10236','lbl_save_answers','1','0','SAVE ANSWERS','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10237','lbl_reset_pass_linkmsg','1','0','Please enter the email address associated with your Bed Bath & Beyond account to receive a reset password link.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10238','lbl_create_pass','1','0','Create Password','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10239','lbl_update_security_question','1','0','UPDATE SECURITY QUESTION','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10240','lbl_add_security_question','1','0','ADD SECURITY QUESTION','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10241','lbl_PoBox_Popup','1','0','We are unable to add this item to your registry because your shipping address contains a P.O.Box','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('DC1bbLTA2910014','notifyRegMessage_mob','1','0','Notify Registrant message displayed','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('DC1bbLTA2900002','notifyRegMessage','1','0','Notify Registrant message displayed','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('DC1bbLTA2930002','lbl_address_Count','1','0','','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('DC1bbLTA2950006','lbl_apply_mob','1','0','Apply','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('DC1bbLTA2950003','lbl_billing_address_mob','1','0','Billing Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('DC1bbLTA2950004','lbl_city_mob','1','0','City','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('DC1bbLTA2950002','lbl_select_country_mob','1','0','Select Country','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('DC1bbLTA2950005','lbl_state_mob','1','0','State','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('DC1bbLTA2950001','lbl_update_billing_info','1','0','Update Your Billing Info','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10146','lbl_creditcard_ending_in','1','0','Ending in ','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10250','lbl_orderinfo_tracking','1','0','Tracking:','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10251','lbl_orderinfo_orderno','1','0','Order No. :','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10256','lbl_orderconf_shipping','1','0','Shipping','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10259','lbl_registry_reasons_to_register','1','0','Reasons to Register','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10260','lbl_registry_search_for','1','0','Search for a Gift Registry','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10265','lbl_spc_creditcard_cvv','1','0','CVV','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10266','lbl_spc_creditcard_mmyy','1','0','mm/yy','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10271','lbl_call_store_for_availability','1','0','Call Store For Availability','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10273','lbl_usually_ships_in_hours','1','0','Shipping Options','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10274','lbl_view_all_stores','1','0','View All Stores','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10277','lbl_search_for_more_listing','1','0','Search For more Listings','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10278','lbl_find_stores','1','0','FIND STORES','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10280','lbl_Not_You','1','0','Not You?','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10281','lbl_registry_complete_profile','1','0','Complete Profile','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10282','lbl_registry_complete_profile_hdr_msg','1','0','Finish adding your shipping address and favorite store to share your registry with friends and family.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10283','lbl_registry_visibility','1','0','Registry visibility:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10284','lbl_registry_visibility_public','1','0','Public','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10285','lbl_registry_visibility_private','1','0','Private','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10286','lbl_registry_public_vs_private','1','0','Public vs. Private','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10287','lbl_registry_public_vs_private_info','1','0','“Public” – Anyone searching for your registry may be able to view your registry & purchase gifts. “Private” – Only you can view your registry -friends and family will not be able to find your registry or purchase gifts from your registry.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10288','lbl_registry_browse_add_gifts','1','0','Browse &amp; Add Gifts','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10289','lbl_registry_share_modal_private_alert','1','0','Please <a href="#" class="editRegInfo">complete your profile</a> to share with friends','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10290','lbl_registry_share_modal_share_title','1','0','Share your registry','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10291','lbl_registry_share_modal_share_subtitle','1','0','with friends &amp; family!','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10292','lbl_registry_share_modal_rec_title','1','0','get recommendations','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10293','lbl_registry_share_modal_rec_subtitle','1','0','from friends &amp; family','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10294','lbl_registry_share_modal_rec_info','1','0','Your friends and family know you best. get helpful advice on what to register for.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10295','lbl_registry_share_modal_copy_url','1','0','COPY URL','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10296','lbl_registry_share_modal_rec_invite_now','1','0','INVITE NOW','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10297','lbl_registry_header_registry_tools','1','0','Registry Tools','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10302','lbl_alternate_number','1','0','Please enter an alternate number','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10310','lblAddToCart','1','0','ADD TO CART','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10311','notifyRegistrantModalMessage','1','0','','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10312','notifyRegistrantModalMessageD','1','0','The item you selected has been discontinued.
 Are you sure you want to add this item to your registry or cart?','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10313','notifyRegistrantModalMessageN','1','0','The item you selected has limited quantity remaining. 
Are you sure you want to add this item to your registry or cart?','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10314','notifyRegistrantModalTitle','1','0','This item has limited quantity remaining or has been discontinued. ','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10315','notifyRegistrantModalTitleD','1','0','Discontinued Item','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10316','notifyRegistrantModalTitleN','1','0','Limited Availability ','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10020','lbl_mob_filter_view_Result','1','0','VIEW RESULTS','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10305','lbl_deactivateRegistry','1','0','Deactivate your registry','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10306','lbl_makeRegistryPublic','1','0','Make my registry public','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10333','lbl_registry_show_hooray_modal','1','0','yes','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10354','lbl_college_info_sheet','1','0','School Info Sheet','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10355','lbl_college_closest_store','1','0','Closest Store','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10356','lbl_reset_your_password','1','0','Reset your password.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10357','lbl_reset_link_expired','1','0','This link has expired. Please try resetting your password again.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10358','lbl_find_in_your_store','1','0','Find in Your Local Store','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10359','lbl_reg_event_date','1','0','event date','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10360','lbl_reg_noOfGuests','1','0','number of guests','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10362','lbl_reg_phone','1','0','phone','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10363','lbl_reg_MobilePhone','1','0','mobile phone','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10364','lbl_reg_createRegistry','1','0','Create Registry','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10365','lbl_reg_wedding_date','1','0','wedding date','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10366','lbl_reg_shower_date','1','0','shower date','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10367','lbl_reg_maiden_name','1','0','maiden name','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10368','lbl_reg_expt_arrival_date','1','0','expected arrival date','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10369','lbl_reg_cell_no','1','0','cell number','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10370','lbl_reg_your_name','1','0','your name','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10371','lbl_reg_existAccount','1','0','You may already have an account. Use your password to continue.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10372','lbl_reg_show','1','0','Show','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10373','lbl_reg_newPassword','1','0','At least one uppercase (a-z) alphabet, 1 number and a minimum of 8 characters','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10374','lbl_reg_inviteCoreg','1','0','Invite your co-registrant to share and manage this registry','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10375','lbl_reg_moving','1','0','Moving? <p class="subText">If you know that after a certain date, you''ll want gifts sent to your new address, you should enter that information now.</p>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10377','lbl_reg_send_gifts','1','0','Please send my gifts to this address too!','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10378','lbl_reg_yes','1','0','yes','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10379','lbl_reg_boy','1','0','boy','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10380','lbl_reg_girl','1','0','girl','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10381','lbl_reg_its_surprise','1','0','it''s a surprise','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10382','lbl_reg_bride','1','0','bride','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10383','lbl_reg_groom','1','0','groom','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10385','lbl_reg_ph_firstname','1','0','First Name','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10386','lbl_reg_ph_lastname','1','0','Last Name','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10387','lbl_reg_ph_maidenname','1','0','Maiden Name','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10388','lbl_reg_ph_emaillogin','1','0','Email (will be used to Login)','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10389','lbl_reg_ph_password','1','0','Password','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10390','lbl_reg_ph_confirmpassword','1','0','Confirm Password','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10391','lbl_reg_ph_coregemail','1','0','Email(optional)','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10392','lbl_reg_ph_date','1','0','mm/dd/yyyy','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10393','lbl_reg_ph_approxNo','1','0','Approximate Number','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10395','lbl_reg_favStore','1','0','your favorite store','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10396','lbl_reg_selectFavStore','1','0','You have not selected a Favorite Store.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10397','lbl_reg_ph_favstore','1','0','Enter city and State or Zip Code','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10398','lbl_reg_ph_futuredate','1','0','Future Date','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10399','lbl_reg_ph_futureaddress','1','0','Future Contact Address','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10400','lbl_reg_ph_contactAdress','1','0','Contact Address','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10401','lbl_reg_ph_shippingAddress','1','0','Shipping Address','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10404','lbl_create_your_reg','1','0','create your registry:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10406','lbl_co_reg','1','0','co-registrant','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10407','lbl_coReg_email','1','0','co-registrant email','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10408','lbl_do_you_know_babyGender','1','0','do you know the gender yet?','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10411','lbl_reg_favStoreSelected','1','0','Based on your state and city or zip code, we''ve selected the following store for you.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10412','lbl_successful_updated','1','0','Item Successfully updated.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10415','notifySliderTitleD','1','0','Discontinued Item','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10416','notifySliderTitleN','1','0','Limited Availability ','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10303','notifyRegMsg_StatusN','1','0','This item has limited quantity remaining. ','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10304','notifyRegMsg_StatusD','1','0','This item has been discontinued','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10419','lbl_stores_within','1','0','stores within','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10420','lbl_field_empty','1','0','Field can''t be empty','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10421','lbl_valid_zip_code','1','0','Please enter a valid US zip Code.We can''t find any pickup locations near the location.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBMAYPS16LTA10001','lbl_oos_find_in_store_aria','1','0','Out Of Stock Online. Find in a store near you.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBMAYPS16LTA10002','find_a_store_bannerlink','1','0','storeLocator','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10422','lbl_dsl_alt_modal_head_sfl','1','0','Please enter information below to add this item to your wishlist','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10423','lbl_college_view_more','1','0','View More','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10424','lbl_college_store_info','1','0','Store Info','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10425','lbl_item_removed_from_list','1','0','Item(s) has been removed from Saved Items','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10188','lbl_exceed_limit_msg','1','0','You  have exceeded limit and email has been sent to your id now','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10427','lbl_display_not_you','1','0','Not You?','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10428','lbl_call_store_for_availability','1','0','Call Store For Availability','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10429','lbl_pick_up_not_available','1','0','Pick up in store is not available at this time','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10430','lbl_usually_ships_in_hours','1','0','Usually Ships in 24 hours','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10431','lbl_view_all_stores','1','0','View Stores','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10432','lbl_store_reserve','1','0','RESERVE NOW','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10434','lbl_search_for_more_listing','1','0','Search For more Listings','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10435','lbl_find_stores','1','0','FIND STORES','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10436','lbl_find_in_your_store','1','0','Find in Your Local Store','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10437','lbl_we_have_found','1','0','We have found','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10438','lbl_stores_within','1','0','stores within','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10439','lbl_stores_within','1','0','stores within','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10440','lbl_find_other_stores','1','0','Find Other stores','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10441','lbl_pick_up_in_your','1','0','Pick Up In Your','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10442','lbl_store','1','0','Store','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10446','lbl_store_route_miles_ca','1','0','km','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10448','lbl_ph_forgotPassword_email','1','0','type your email address','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10449','lbl_type_your_answer','1','0','type your answer here','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10451','notifyRegMsg_StatusD','1','0','This item has been discontinued.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10452','notifyRegMsg_StatusN','1','0','This item has limited quantity remaining. ','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10453','lbl_sign_in_to_save','1','0','Sign in to save item to your list.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10454','lbl_view_saved_items','1','0','View List','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10455','lbl_SAVED','1','0','SAVED','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10456','lbl_Multi_ship','1','0','Multiship','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10457','lbl_apt_optional','1','0','Apt # (Optional)','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10450','lbl_pick_up_in_your','1','0','Pick Up In Your','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10641','lbl_reset_email','1','0','Email','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10646','lbl_reg_ph_apt_building','1','0','Apt/Bldg (optional)','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10470','reg_lbl_ph_nursery_decor_theme','1','0','Nursery Decor','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10648','lbl_registry_store_pref_store','1','0','Based on your preferences in your profile.You have selected the following store','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10640','lbl_reg_all_fields_required','1','0','All fields are required','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10384','lbl_reg_ph_fullname','1','0','Full Name(First Name, Last Name)','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10403','lbl_your_info','1','0','your info','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10463','lbl_add_to_bbb_emails','1','0','add me to the Bed Bath and Beyond emails to receive exclusive email offers and news.  You can unsubscribe at anytime.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10464','lbl_registry_address','1','0','address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10465','lbl_all','1','0','ALL','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10466','lbl_all_addresses','1','0','All Addresses','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10467','lbl_alternate_phone_no','1','0','Alternate Phone','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10468','lbl_email_sent','1','0','An email been sent to','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10469','lbl_apartment_number','1','0','Apartment Number (Optional)','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10471','lbl_arrival_date','1','0','arrival date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10472','lbl_password_condition','1','0','at least 1 uppercase (a-z) alphabet 1 number and a minimum of 8 characters','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10473','lbl_baby_name','1','0','baby name','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10474','lbl_back_to_cart','1','0','BACK TO CART','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10475','lbl_Barcode_Here','1','0','Barcode Here','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10476','lbl_Barcode_Number','1','0','Barcode Number Here','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10477','lbl_reg_current_location','1','0','Based on your state and city or zip code.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10478','lbl_bbb','1','0','Bed Bath & Beyond','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10479','lbl_custom_category_bed_sheets','1','0','BED SHEETS','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10480','lbl_bbb_canada','1','0','BedBath Canada','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10481','lbl_custom_category_bed','1','0','BEDS','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10482','lbl_billing_info_internationally','1','0','Billing Internationally','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10483','lbl_boy','1','0','boy','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10484','lbl_bbbaby','1','0','BuyBuy Baby','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10485','lbl_CHANGE_STORE','1','0','CHANGE STORE','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10486','lbl_registry_college','1','0','College/University','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10487','lbl_congratulations','1','0','Congratulations','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10488','lbl_contact_address_required','1','0','contact address is required','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10489','lbl_co_registrant','1','0','co-registrant','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10491','lbl_create_registry','1','0','create your registry','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10492','lbl_distance','1','0','Distance:','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10493','lbl_do_you_know_gender','1','0','do you know the gender yet?','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10494','lbl_edit_profile','1','0','EDIT YOUR PROFILE','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10495','lbl_eligiblefor_gift','1','0','Eligible for Gift Packaging','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10496','lbl_email_addr_req','1','0','Email address is required','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10497','lbl_email_password_change','1','0','email and/or password will change Lorem Ipsum dolor emsit amet,your name','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10498','lbl_enter_new_password','1','0','Enter New Password','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10499','lbl_security_code','1','0','Enter Security Code:','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10500','lbl_error_occured','1','0','Error Occurred','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10501','lbl_fav_store','1','0','favorite store','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10502','lbl_friend_reg','1','0','Friend''s Registries','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10503','lbl_future_date','1','0','Future date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10504','lbl_future_ship_address','1','0','Future Shipping Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10505','lbl_Merchandise_gifts','1','0','Gift Card/Merchandise Credit','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10506','lbl_girl','1','0','girl','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10507','lbl_giving_gift','1','0','Giving a Gift','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10508','lbl_happy_annual_day','1','0','Happy Annual Day','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10509','lbl_instock_readytoship','1','0','In stock online and ready to ship','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10510','lbl_incorrect_security_question','1','0','Incorrect Security Answer','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10511','lbl_In-stock','1','0','In-stock','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10512','lbl_item_successfully_added','1','0','Item(s) successfully added to your registry','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10513','lbl_items_willbe_added','1','0','Items Will be Added to Your','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10514','lbl_reg_its_surprise','1','0','it''s a surprise','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10515','lbl_less','1','0','LESS','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10516','lbl_limited_stock','1','0','Limited Stock','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10517','lbl_load_more_pages','1','0','Load more pages','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10518','lbl_make_registry_public','1','0','MAKE MY REGISTRY PUBLIC','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10519','lbl_pref_mail_address','1','0','Make Preferred Mailing Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10520','lbl_max_surcharge','1','0','Maximum Surcharge Reached','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10521','lbl_mobile','1','0','mobile','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10522','lbl_modify_name','1','0','Modifying your name','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10523','lbl_more','1','0','MORE','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10524','lbl_registry_moving','1','0','Moving? After a certain date send gifts to my new address.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10526','lbl_notify_me','1','0','Notify me when this item is available','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10527','lbl_nursery_theme','1','0','nursery theme','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10528','lbl_order_now','1','0','Order now and have your items delivered on a specific date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10529','lbl_order_review','1','0','Review Order','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10530','lbl_out _of_stock','1','0','Out of Stock Online','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10531','lbl_pack_and_hold','1','0','Pack & Hold','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10532','lbl_payment_methods','1','0','Payment Methods','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10533','lbl_pickupinstore','1','0','PICK UP IN STORE','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10534','lbl_pickup_not_intl_customers','1','0','Pick up in store is not available for International Customers','MobileWeb'); 
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10535','lbl_pick_upd_where_left','1','0','PICK UP RIGHT','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10536','lbl_place_order_now','1','0','Place Order Now','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10537','lbl_lbl_enter_address','1','0','Please enter the address, city, state where you will be starting.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10538','lbl_select_sku','1','0','Please select size/color/finishing choices (if available) of selected products.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10539','lbl_spc_submit_all_forms','1','0','Please submit all required forms in shipping, billing and payment and review your order before you place your order.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10540','lbl_radius_values','1','0','Radius Values','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10541','lbl_redeem_in_store','1','0','REDEEM IN-STORE','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10542','lbl_requested','1','0','Requested','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10543','lbl_save_for_later','1','0','Save for Later','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10544','lbl_save_password','1','0','SAVE PASSWORD','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10545','lbl_select_address','1','0','Select an Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10546','lbl_select_method','1','0','Select Method','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10547','lbl_registry_type','1','0','SELECT REGISTRY  TYPE','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10548','lbl_select_service_cart','1','0','select service level to add to cart','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10549','lbl_select_service','1','0','select service level to add to registry ','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10550','lbl_select_service_wishlist','1','0','select service level to save it to wishlist','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10551','lbl_billing_select_state','1','0','Select State','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10552','lbl_select_store','1','0','SELECT STORE','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10553','lbl_ship_this_item','1','0','SHIP THIS ITEM','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10554','lbl_shipped_to','1','0','Shipped To','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10555','lbl_shipping_shipping_option_mob','1','0','Shipping Options','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10556','lbl_show_date','1','0','show date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10558','lbl_skip_questions','1','0','skip questions','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10559','lbl_standard_shipping','1','0','Standard Shipping $5.99 (2 Bus Day)','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10560','lbl_state_dropdown','1','0','State Dropdown','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10561','lbl_store_pickup','1','0','Store Pick up Not Available','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10562','lbl_thank_you','1','0','Thank You','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10563','lbl_email_thanks','1','0','thanks','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10564','lbl_try_video_again','1','0','The video can''t be played at the moment. Please try after some time.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10565','lbl_time','1','0','Time:','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10566','lbl_total_surcharge','1','0','Total Surcharge Savings','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10567','lbl_trigger_slider','1','0','triggerSlider','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10568','lbl_type_password','1','0','type password','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10569','lbl_type_ans_here','1','0','type your answer here','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10570','lbl_update_contact_info','1','0','Update Valid Contact Info','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10571','lbl_update_shipping_info','1','0','Update Valid Shipping Info','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10572','lbl_use_ship_address','1','0','Use as my Shipping Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10573','lbl_current_location','1','0','use my current location','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10574','lbl_view_manage','1','0','View and Manage Cart','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10576','lbl_welcome_mate','1','0','Welcome Mate','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10577','lbl_selected_store','1','0','We''ve selected the following store for you.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10579','lbl_COPY_REGISTRY','1','0','YES, COPY THIS REGISTRY','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10580','lbl_no_saved_address_message','1','0','You have not saved any addresses. Click here to set one up.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10581','lbl_account_already','1','0','You may have an account already. Please provide your password to Login.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10582','lbl_your_billing_info','1','0','Your Billing Info','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10583','lbl_your_event','1','0','your event','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10584','lbl_your_fav_store','1','0','your favorite store','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10586','lbl_your_name','1','0','your name','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10587','lbl_ship_date','1','0','Shipping Date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10588','lbl_find_store_enter_address','1','0','Find Store  Enter Zip Code Address or City and State is required','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10589','lbl_apt_num','1','0','Apt #','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10590','lbl_city_small','1','0','city','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10591','lbl_state_small','1','0','state','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10592','lbl_congrats_grad','1','0','Congrats, Grad','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10593','lbl_Thanks','1','0','Thanks','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10594','lbl_no_wallet_found','1','0','No Wallet Found.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10595','lbl_use_anywhere','1','0','Use Anywhere','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10596','lbl_back_to_kickstarter','1','0','BACK TO KICKSTARTER','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10597','lbl_usd','1','0','USD','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10598','lbl_cad','1','0','CAD','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10599','lbl_start','1','0','start','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10600','lbl_view_order_details_cap','1','0','VIEW ORDER DETAILS','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10601','lbl_we_found','1','0','We found','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10602','lbl_in_your_area','1','0','in your area.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10603','lbl_available_for_pickup','1','0','Available for pickup at the','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10604','lbl_store_reserve_online','1','0','Store. Online only offers cannot be applied to reserve online.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10605','lbl_items','1','0','item(s)','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10606','lbl_cart_empty','1','0','Your Cart (0)','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10607','lbl_out_of_stock1','1','0','Out of Stock','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10608','lbl_in_stock1','1','0','In Stock','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10609','lbl_ship_thisitem','1','0','Ship this item','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10610','lbl_express_checkout','1','0','Express Checkout','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10611','lbl_From','1','0','From','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10612','lbl_REG_TYPE','1','0','REGISTRY TYPE','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10613','lbl_SELECT_STATE','1','0','SELECT STATE','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10614','lbl_View_registry','1','0','View Registry','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10615','lbl_mob_calendar','1','0','calendar','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10616','lbl_bride','1','0','bride','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10617','lbl_groom','1','0','groom','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10618','lbl_invite_friends_for_registry','1','0','Invite your co-registrant to share and manage this registry.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10619','lbl_reg_shower_date','1','0','shower date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10620','lbl_phone_small','1','0','phone','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10624','lbl_not_available_ltl','1','0','Not Available for LTL','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10625','lbl_enter_numeric_value','1','0','Please enter a numeric value between 1-99','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10626','lbl_qty','1','0','Qty','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10627','lbl_available_for_pickup_at_the','1','0','Available for pickup at the--','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10628','lbl_online_offers_applied','1','0','Online only offers cannot be applied to reserve online.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10629','lbl_checkout_error_first_name','1','0','First name is required.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10630','lbl_checkout_error_last_name','1','0','last name is required.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10631','lbl_checkout_error_address','1','0','Shipping Address is Required','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10632','lbl_checkout_error_phone_number','1','0','please enter a valid phone number.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10633','lbl_checkout_error_email','1','0','email is required.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10634','lbl_checkout_error_chars_only','1','0','Please enter letters only','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10635','lbl_checkout_error_num_only','1','0','Please enter numbers only','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10636','lbl_checkout_error_cc_num_req','1','0','Credit Card Number is required.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10637','lbl_checkout_error_valid_exp_date','1','0','Please enter a valid expiration date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10638','lbl_checkout_error_cvv','1','0','Please enter at least 3 characters.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('DC1bbLTA2740001','lbl_registry_update_modal_shipping_alert','1','0','You must provide your shipping address to purchase registry items.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10650','lbl_email_sign_up','1','0','Email Sign Up','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10651','lbl_special_offers','1','0','For Special Offers','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10652','lbl_special_email','1','0','Special Email','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10653','lbl_your_privacy','1','0','Your Privacy','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10654','lbl_satisfaction_gurantee','1','0','100% Satisfaction Guarantee','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10655','lbl_return_anything','1','0','You can return anything bought online either through the mail or any store. Help is always available at 1-800-GO-BEYOND (1-800-462-3966)','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10656','lbl_about_us','1','0','About Us','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10657','lbl_corporate_responsibilty','1','0','Corporate Responsibility','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10658','lbl_media_relations','1','0','Media Relations','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10659','lbl_investor_relations','1','0','Investor Relations','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10660','lbl_Careers','1','0','Careers','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10661','lbl_terms_of_use','1','0','Terms of Use','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10662','lbl_corporate_sales','1','0','Corporate Sales','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10663','lbl_glossary','1','0','Glossary','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10664','lbl_shop_personalized_inv','1','0','Shop Personalized Invitations','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10665','lbl_shop_gift_cards','1','0','Shop Gift Cards','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10666','lbl_shop_clearance','1','0','Shop all Clearance','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10667','lbl_feedback','1','0','Feedback','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10668','lbl_shipping_info','1','0','Shipping Info','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10669','lbl_faqs','1','0','FAQs','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10670','lbl_safety_recalls','1','0','Safety & Recalls','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10671','lbl_follow_us','1','0','Follow Us','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10672','lbl_facebook','1','0','Facebook','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10673','lbl_twitter','1','0','Twitter','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10674','lbl_face_value','1','0','Face Value','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10675','lbl_bb_baby','1','0','Buy Buy Baby','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10676','lbl_bbb_desktop','1','0','Bed Bath & Beyond','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10677','lbl_bbb_rights_reserved','1','0','Bed Bath & Beyond Inc. and its subsidiaries. All rights reserved.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10678','lbl_wedding_invites','1','0','Wedding Invitations','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10679','lbl_we_love_university','1','0','We Love University','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10680','lbl_we_love_college','1','0','We Love College','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10681','lbl_submit_search','1','0','submit your search','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10682','lbl_personalization_lost','1','0','When you close the modal your personalization will be lost and youll be taken to base sku','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10683','lbl_return_to_base_sku','1','0','Return to Personalization or base sku','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10684','lbl_block_small','1','0','block','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10685','lbl_none','1','0','none','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10686','lbl_email_friend','1','0','email a friend','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10687','lbl_change_item','1','0','Change Item','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10688','lbl_product_link_in_tweet','1','0','Product Link has been included in the tweet','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10689','lbl_charac_count_accounted','1','0','and character count has been accounted for.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10690','lbl_some_fun_products','1','0','Some Fun New Products','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10692','lbl_password_changed','1','0','Password successfully changed','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10693','lbl_mail_success','1','0','Mail Send Successfull','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10694','lbl_want_more','1','0','WANT MORE','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10695','lbl_coupons','1','0','COUPONS?','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10696','lbl_sign_up_for','1','0','Sign Up for','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10697','lbl_email_coupons','1','0','Email Coupons','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10698','lbl_sms_coupons','1','0','SMS Coupons','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10699','lbl_user_exists','1','0','User already exist.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10700','lbl_preferred_mail_address','1','0','Make preferred Mailing Address','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10701','lbl_go_to_registries','1','0','Go To My Registries','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10702','lbl_quest1','1','0','Question1','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10703','lbl_answer1','1','0','Answer1','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10704','lbl_question2','1','0','Question2','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10705','lbl_answer2','1','0','Answer2','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10706','lbl_challange_quest','1','0','Challenge Question','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10707','lbl_select_first_quest','1','0','Please select First Question','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10708','lbl_select_second_quest','1','0','Please select Second Question','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10709','lbl_change_password','1','0','Change Password','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10710','lbl_buy_buy_Baby','1','0','buybuy BABY','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10711','lbl_stores','1','0','Stores','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10712','lbl_temp_password','1','0','Temporary Password','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10713','lbl_policies','1','0','Policies','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10714','lbl_visit_full_site','1','0','Visit Full Site','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10715','lbl_help_avail_at','1','0','Help is always available at','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10716','lbl_help_num','1','0','1-877-3-BUY- BABY (1877-328-9222)','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10717','lbl_help_num2','1','0','1999-2013 Buy Buy Baby','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10718','lbl_rights_reserved','1','0',' Inc.All rights reserved.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10719','lbl_query_assistance','1','0','For any questions or assistance call','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10720','lbl_help_bbb','1','0','1-800-GO BEYOND','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10721','lbl_help_num_bbb','1','0','(1-800-462-3966)','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10722','lbl_help_num2_bbb','1','0','1999-2013 Bed Bath','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10723','lbl_inc_subsidiaries','1','0','Beyond Inc. and its subsidiaries','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10724','lbl_all_right_reserved','1','0','All rights reserved.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10725','lbl_success_login','1','0','Successfully Logged in','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10726','lbl_benefits','1','0','Benefits','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10727','lbl_of_account','1','0','of an account','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10728','lbl_checkout_speed','1','0','Speed up the checkout process','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10729','lbl_prod_reviews','1','0','Write product reviews','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10730','lbl_track_order_history','1','0','Track order history','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10731','lbl_maintain_billing_address','1','0','Maintain billing addresses and billing info','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10732','lbl_facebook_connect','1','0','Connect your account to Facebook for faster registration','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10733','lbl_create_password','1','0','Create Password','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10735','lbl_unexpected_error','1','0','Unexpected Error occurred','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10736','lbl_contact_admin','1','0','Please contact system administrator.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10737','lbl_upgrade_flash','1','0','You need to upgrade your Adobe Flash Player','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10738','lbl_download_flash','1','0','Download the free Adobe Flash Player now','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10739','lbl_enable_js','1','0','If you have already installed the latest version of Adobe Flash Player and you still see this message then refer to your browser''s documentation to learn how to enable JavaScript.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10740','lbl_forgot_password','1','0','Forgot Password','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10741','lbl_browse_guest','1','0','Browse without signing in','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10742','lbl_setup_challnege_quest','1','0','Set up your challenge question for more account privacy.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10743','lbl_start_now','1','0','Start Now','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10744','lbl_place_order','1','0','Place Order','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10745','lbl_opt_in','1','0','Opt-In','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10746','lbl_open_small','1','0','open','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10747','lbl_close_small','1','0','close','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10748','lbl_reg_login_page','1','0','Register Login Page','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10749','lbl_login_email','1','0','Login(Email)','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10750','lbl_message_here','1','0','message here','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10751','lbl_error_occured','1','0','Some error occured','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10752','lbl_success_proceed','1','0','Success! Please proceed','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10753','lbl_next_collection','1','0','NEXT COLLECTION:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10754','lbl_pinterest','1','0','Follow Pinterest''s board Official news on Pinterest.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10755','lbl_for_small','1','0','for','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10756','lbl_guests','1','0','Guests','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10757','lbl_registry_page','1','0','Registry Page','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10758','lbl_guides_adivce_landing','1','0','Guides And Advice Landing','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10759','lbl_college_guide','1','0','College Guide','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10760','lbl_free_goody_bag','1','0','Free Goody Bag','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10761','lbl_prod_not_found','1','0','Product Not found','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10762','lbl_sku_id','1','0','Sku Id','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10763','lbl_prod_id','1','0','Product Id','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10764','lbl_oos_email','1','0','Out Of Stock Email','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10765','lbl_recent_purchase','1','0','If you have recently purchased an item that offered a mail-in rebate and the not mailed it in yet,please download and print the rebate from and submit for processing.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10766','lbl_rebate_form','1','0','Rebate Form for all other Rebates','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10767','lbl_rebate_questions','1','0','If you have questions or would like to track your rebate, please visit,','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10768','lbl_service_levels','1','0','Service Levels','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10769','lbl_curbside','1','0','Curbside','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10770','lbl_threshold','1','0','Threshold','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10771','lbl_room_of_choice','1','0','Room Of Choice','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10772','lbl_white_glove','1','0','White Glove','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10773','lbl_flexible_delivery_time','1','0','Flexible Delivery Times','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10774','lbl_unloading_service','1','0','Unloading Service','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10775','lbl_bring_item_inside','1','0','Bring Item Inside','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10776','lbl_set_item_desired_room','1','0','Set Item In Desired Room','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10777','lbl_unboxing','1','0','Unboxing','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10778','lbl_packaging_removal','1','0','Packaging Removal','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10779','lbl_assembly_optional','1','0','Assembly Optional (Additional Fee)','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10780','lbl_moved_to_registry','1','0','has been moved to registry','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10781','lbl_email_sent','1','0','An email has been sent to recipient@domain.com and you will get a follow up email when the open Space table lamp is available in Stock for shipping.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10782','lbl_email_test','1','0','Email Test','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10783','lbl_thanks_signing_up','1','0','Thanks for signing up!','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10784','lbl_first_time_subscriber','1','0','If you are a first-time subscriber your offer will arrive within a week!','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10785','lbl_available1','1','0','Available','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10786','lbl_at_your_store','1','0','at your favorite store','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10788','lbl_prod_description','1','0','Product Description','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10789','lbl_view_registry','1','0','View your registry','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10790','lbl_exclusive','1','0','Exclusively Ours','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10791','lbl_DISMISS','1','0','DISMISS','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10792','lbl_add_new_billing_address','1','0','Add new billing address','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10793','lbl_checkout_billing_coupons','1','0','Checkout - Billing - Coupons','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10794','lbl_checkout_error_cvv','1','0','Please enter at least 3 characters.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10795','lbl_Country','1','0','Country','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10797','lbl_one_or_more_errors','1','0','There were 1 or more errors. Please correct these errors and try again.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10798','lbl_Add_Assembly','1','0','Add Assembly','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10799','lbl_Add_new_billing','1','0','Add new billing','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10800','lbl_Selected_international_Billing_Address','1','0','Selected/Added International Billing Address is Restricted','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10801','lbl_Exp_Month','1','0','Exp Month','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10802','lbl_Exp_Year','1','0','Exp Year','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10803','lbl_Add_new_billing_address_select_payment','1','0','Please add a new billing address to enable and select a form of payment.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10804','lbl_test_order','1','0','This is a test order and will not get delivered.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10805','lbl_Existing_Customers','1','0','Existing Customers','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10806','lbl_Gift_Card_Amount_Applied','1','0','Gift Card Amount Applied:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10807','lbl_No_Bridal_Shows_Avialable','1','0','No Bridal Shows Avialable','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10808','lbl_No_College_Events_available','1','0','No College Events available','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10809','lbl_looking_for_a','1','0','looking for a','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10810','lbl_enter_registrant''s_information','1','0','Please enter the registrant''s information','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10811','lbl_Somethings_wrong_try_again','1','0','Somethings wrong :Please try again','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10812','lbl_Incl_White_Glove','1','0','Incl White Glove','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10813','lbl_With_Assembly','1','0','With Assembly:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10814','lbl_added_to_your_registry','1','0','has been added to your registry.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10815','lbl_has_been_declined','1','0','has been declined','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10816','lbl_Invite_Via_Email','1','0','Invite Via Email','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10817','lbl_from_cart','1','0','from cart','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10818','lbl_Expand_All','1','0','Expand All','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10819','lbl_Collapse_All','1','0','Collapse All','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10820','lbl_arrival_date','1','0','arrival date','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10821','lbl_Get_Map_Directions','1','0','Get Map & Directions','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10822','lbl_copyright_rights_reserved','1','0','©1999-2011 Bed Bath & Beyond Inc. and its subsidiaries. All rights reserved.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10823','lbl_Security_Error','1','0','Security Error','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10824','lbl_not_valid_atg_order_id','1','0','Not a valid ATG Order Id','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10825','lbl_ATG_Order_ID','1','0','ATG Order ID:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10826','lbl_Profile_ID','1','0','Profile ID:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10827','lbl_IP_Address','1','0','IP Address:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10828','lbl_Affiliate','1','0','Affiliate:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10829','lbl_School','1','0','School:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10830','lbl_School_Promo','1','0','School Promo:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10831','lbl_Delivery_Order','1','0','Delivery Order #:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10832','lbl_Shipping_Group_ID','1','0','Shipping Group ID:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10833','lbl_Shipping_Group_Type','1','0','Shipping Group Type:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10834','lbl_item(s)_shipping_to','1','0','item(s) shipping to','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10835','lbl_Item(s)_to_be_picked_up','1','0','Item(s) to be picked up','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10836','lbl_Status_Item(s)_ready_to_be_picked','1','0','Status : Item(s) ready to be picked up on','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10837','lbl_Payment_Group_ID','1','0','Payment Group ID:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10838','lbl_Payment_Group_Type','1','0','Payment Group Type:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10839','Payment_Group_Auth_Code','1','0','Payment Group Auth Code:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10840','lbl_Error_while_validating_email','1','0','Error occurred while validating email.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10841','lbl_Font_Size','1','0','Font Size:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10842','lbl_Select_Font','1','0','Select a Font:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10843','lbl_Font_Color','1','0','Font Color:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10844','lbl_Directions','1','0','Directions','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10845','lbl_Click_to_launch_click_To_chat','1','0','Click here to launch click To chat','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10846','lbl_Welcome_click_to_chat_landing_page','1','0','Welcome to click to chat landing page!','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10847','lbl_To_Activate_offer','1','0','To Activate an offer','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10848','lbl_enter_information_from_offer_into_form','1','0','enter the information from the offer into the form below.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10849','lbl_Offer_Number','1','0','Offer Number','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10850','lbl_Please_type_words_you_see_in_image','1','0','Please type the words you see in the image below.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10851','lbl_send_offers_straight_to_inbox','1','0','send me offers straight to my inbox!','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10852','lbl_send_offers_via_text_message','1','0','send me offers via text message','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10853','lbl_Data_rates_may_apply','1','0','Data rates may apply.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10854','lbl_Email_Privacy','1','0','Email Privacy','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10855','lbl_protection_email_credit_card_information','1','0','For your protection please do not email credit card or other sensitive information.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10856','lbl_Email_Unsubscribe','1','0','Email Unsubscribe','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10857','lbl_do_not_want_receive_email','1','0','If at any time you do not want to receive email','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10858','lbl_circulars_us_subsidiaries','1','0','and/or circulars from us and our subsidiaries','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10859','lbl_remove_from_email_list','1','0','you can always remove yourself from our email list by entering your information in our','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10860','lbl_question_about_offer','1','0','If you have any questions about this offer','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10861','lbl_please_call_us_at','1','0','please call us at 1-800-GO BEYOND® (1-800-462-3966)','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10862','lbl_email_us_at','1','0','or email us at','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10863','lbl_Question','1','0','Question','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10864','lbl_Answer','1','0','Answer','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10865','lbl_password_change_completed','1','0','Your password change is completed successfully !','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10866','lbl_Bridal_Registry','1','0','Bridal Registry','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10867','lbl_ALL_DSK','1','0','ALL','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10868','lbl_products_lowercase_dsk','1','0','products','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10869','lbl_activate_your_online_offer','1','0','Activate Your Online Offer','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10870','lbl_direct_mail_Unsubscribe','1','0','Direct Mail Unsubscribe','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10871','lbl_Daily_dsk','1','0','Daily','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10872','lbl_Weekly_dsk','1','0','Weekly','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10873','lbl_Monthly_dsk','1','0','Monthly','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10874','lbl_Never_dsk','1','0','Never','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10875','lbl_Item_Price_dsk','1','0','Item Price','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10876','lbl_Find_registry_item_guest','1','0','Find','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10877','lbl_Full_Name_dsk','1','0','Full Name:','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10878','lbl_Recommend_Things_dsk','1','0','Recommend Things','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10879','lbl_for_your_friend''s_registry','1','0','for your friend''s registry','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10880','lbl_Get_Started_dsk_recomm','1','0','Get Started','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10881','lbl_Already_have_an_account_recomm','1','0','Already have an account','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10882','lbl_Check_Out_Their_Registry','1','0','Check Out Thier Registry','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10883','lbl_Recommended_Items','1','0','Recommended Items','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10884','lbl_recommending_dsk_recomm','1','0','recommending!','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10885','lbl_Invite_Your_Friends','1','0','Invite Your Friends','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10886','lbl_Get_Their_Input','1','0','Get Their Input','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10887','lbl_Add_Items_to_Your_Registry','1','0','Add Items to Your Registry','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10888','lbl_Manage_Your_Recommenders','1','0','Manage Your Recommenders','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10889','lbl_Get_Recommendations_from_friends_family','1','0','Get Recommendations from friends and family','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10276','lbl_edit_favorite_store','1','0','Edit Favorite Store','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10890','lbl_reset_email','1','0','Email','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10891','lbl_forgotPasswordModelMessage','1','0','Enter your email address below. We will reset your password and send details to the email address you enter.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10892','lblforgotPasswordFromRegistry','1','0','forgot your password','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10894','lbl_resetEmail_sent','1','0','Please check the email associated with your account','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10895','lbl_registry_browse_add_gifts_tab','1','0','BROWSE &amp; ADD GIFTS','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10896','lbl_registry_owner_checklist_tab','1','0','CHECKLIST','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10897','lbl_registry_owner_registry_favorites','1','0','registry favorites','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10898','lbl_registry_owner_gift_ideas','1','0','gift ideas','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10899','lbl_in_your_area','1','0','in your area','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10900','lbl_item_added_cart','1','0','1 item(s) has been added to your Cart!','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10901','lbl_add','1','0','ADD','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10903','lbl_src_your_info','1','0','your info','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10904','lbl_src_your_fav_store','1','0','your favorite store','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10905','lbl_src_your_event_info','1','0','your event','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10906','lbl_src_wedding','1','0','Wedding','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10907','lbl_src_use_as_shipping','1','0','Use as my Shipping Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10908','lbl_src_store_info','1','0','Store Info','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10909','lbl_src_shower_date','1','0','shower date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10910','lbl_src_show_date_info','1','0','show date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10911','lbl_src_show_date','1','0','show date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10912','lbl_src_shipping_address_info','1','0','shipping address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10913','lbl_src_shipping_address','1','0','shipping address is required','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10914','lbl_src_phone_num','1','0','phone','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10915','lbl_src_nursery_theme_info','1','0','nursery theme','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10916','lbl_src_nursery_theme','1','0','Nursery Theme','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10917','lbl_src_mobile_num','1','0','mobile','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10918','lbl_src_know_the_gender','1','0','do you know the gender yet?','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10919','lbl_src_house_warming','1','0','Housewarming','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10920','lbl_src_groom','1','0','groom','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10921','lbl_src_gender_yes','1','0','yes','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10922','lbl_src_gender_suprise','1','0','it''s a suprise','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10923','lbl_src_gender_girl','1','0','girl','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10924','lbl_src_gender_boy','1','0','boy','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10925','lbl_src_future_ship_info','1','0','Moving? After a certain date send gifts to my new address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10926','lbl_src_future_ship_date','1','0','Future date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10927','lbl_src_future_ship_address','1','0','Future Shipping Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10928','lbl_src_find_a_store','1','0','FIND STORE','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10929','lbl_src_favorite_store_info','1','0','favorite store','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10930','lbl_src_favorite_store','1','0','Favorite Store','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10931','lbl_src_fav_store_msg','1','0','Based on your state and city or zip code, we''ve selected the following store for you.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10932','lbl_src_fav_store_hours','1','0','Store hours','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10933','lbl_src_fav_store_directions','1','0','Directions','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10934','lbl_src_fav_store_call','1','0','Call','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10935','lbl_src_existing_acc','1','0','You may have an account already. Please provide your password to Login.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10936','lbl_src_event_shower_date','1','0','Shower Date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10937','lbl_src_event_eventDate','1','0','event date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10938','lbl_src_event_arrival_date','1','0','arrival date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10939','lbl_src_createreg_pwd_msg','1','0','at least 1 uppercase (A-Z) alphabet 1 number and a minimum of 8 characters','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10940','lbl_src_create_registry_btn','1','0','Create Registry','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10941','lbl_src_create_reg','1','0','create your registry','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10942','lbl_src_coreg_info','1','0','Invite your co-registrant ot share and manage this registry','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10943','lbl_src_coreg_fullname','1','0','First Name, Last Name','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10944','lbl_src_coreg_email','1','0','Co-registrant Email','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10945','lbl_src_coreg','1','0','co-registrant','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10946','lbl_src_contact_address','1','0','contact address is required','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10947','lbl_src_commitment','1','0','Commitment Ceremony','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10948','lbl_src_col_univ','1','0','College/University','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10949','lbl_src_change_store','1','0','Change Store','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10950','lbl_src_bride','1','0','bride','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10951','lbl_src_baby_name_info','1','0','baby name','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10952','lbl_src_baby_name','1','0','baby name','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10953','lbl_src_baby','1','0','Baby','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10954','lbl_src_arrival_date_info','1','0','arrival date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10955','lbl_src_apt_no_shipping','1','0','Apartment Number (Optional)','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10956','lbl_src_apt_no_future_ship','1','0','Apartment Number (Optional)','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10957','lbl_src_apt_no','1','0','Apartment Number (Optional)','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10958','lbl_src_address_info','1','0','address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10959','lbl_src_add_to_offers','1','0','Yes, add me to the Bed Bath and Beyond emails to recieve exclusive email offers and news. You can unsubscribe at anytime','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10787','lbl_refine_search','1','0','Please enter your zip code or city and state to find stores in your area.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10966','lbl_no_orders_found','1','0','No orders found','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10968','lbl_update_modal_registry_status_alert','1','0','Your Registry is currently private','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10969','lbl_update_modal_opt_in_and_out_msg','1','0','Yes, please include my registry in search results on 3rd party sites such as TheKnot.com or TheBump.com','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10970','lbl_update_modal_modify_profile_alert','1','0','Modifying your name or email address on your registry will change this information for your account as well','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10971','lbl_update_modal_make_public_msg','1','0','Make my registry public','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10972','lbl_update_modal_make_private_msg','1','0','Make my registry private','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10973','lbl_update_modal_edit_profile','1','0','Edit your profile','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10974','lbl_update_modal_complete_registry_text','1','0','Complete and save your registry so that guests can find it & purchase gifts.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10975','lbl_update_modal_change_public_no_worries_msg','1','0','Don''t worry, you''ll still be able to make your registry public','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10976','lbl_update_modal_change_private_no_worries_msg','1','0','Don''t worry, you''ll still be able to make your registry private','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10977','lbl_src_update_reg','1','0','Update Registry','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10978','lbl_src_update_info','1','0','Modifying your name or email address on your registry will change this information for your account as well','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10979','lbl_src_third_party_optin','1','0','Yes, please include my registry in search results on 3rd party sites such as TheKnot.com or TheBump.com','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10980','lbl_src_make_reg_public','1','0','Make my registry public','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10981','lbl_src_make_reg_private','1','0','Make my registry private','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10982','lbl_src_edit_heading','1','0','Edit your profile','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10983','lbl_registry_completion_msg_factor','1','0','2.5','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10984','lbl_spc_order_total_offers','1','0','Order Total with offer(s):','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10647','lbl_nursery_decor_theme','1','0','nursery d&#233;cor or theme','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10272','lbl_pick_up_not_available','','0','Pickup InStore is Not Available.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10275','lbl_store_reserve','1','0','Reserve','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10418','lbl_we_have_found','1','0','We found','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA109889','lbl_registry_completion_msg_no_guests','1','0','Please <a href="#" class="editRegInfo">enter the number of guests</a> so we can tell you how many gifts to register for, using math. ','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBMAYPS16LTA10004','lbl_feature_tags_most_popular','1','0','Most Popular','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBMAYPS16LTA10005','lbl_feature_tags_most_viewed','1','0','Trending','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBMAYPS16LTA10006','lbl_feature_tags_new','1','0','New','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBMAYPS16LTA10007','lbl_feature_tags_sponsored','1','0','Sponsored','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBMAYPS16LTA10008','lbl_feature_tags_top_rated','1','0','Top Rated','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10990','lbl_review_for_the_product','1','0','reviews for the product','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10991','lbl_click_to_play_video','1','0','click to play video for','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10992','lbl_for_the_product','1','0','for the product','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10993','lbl_expand','1','0','expand','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10994','lbl_collapse','1','0','collapse','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10443','lbl_store_route_miles','1','0','miles','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10405','lbl_account_info','1','0','account info <span class="required"> (*required to manage your registry)</span>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10376','lbl_reg_useAsShpiping','1','0','Use as my shipping address','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10394','lbl_reg_ph_phone','1','0','(___) ___-____','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10402','lbl_reg_moving_store','1','0','<span>Moving?</span> If you know that after a certain date, you''ll want gifts sent to a new address, you should enter that information now.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10361','lbl_reg_contactAddress','1','0','contact address','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10996','lbl_remove_saved_item','1','0','tap to remove from saved items','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10997','lbl_product_img_for','1','0','product image for','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10998','lbl_not_yet_rated','1','0','not yet rated','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10999','lbl_remove_from_saved_items','1','0','tap to remove this product from your saved items','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11000','lbl_remove_from_cart','1','0','tap to remove this product from your cart','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA109914','lbl_coregistrantEmail_validationMessage','1','0','You''ve entered your own email address.Please enter your co-registrant''s email address.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA109915','lbl_src_ship_addr_update','1','0','Update Your Shipping Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA109916','lbl_src_contact_address_update','1','0','Update Your Contact Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA109917','lbl_future_addr_update','1','0','Update Future Shipping Address ','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA109918','lbl_addr_apply_btn','1','0','Apply','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10230','lbl_question_2','1','0','Question 2','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10228','lbl_question_1','1','0','Question 1','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10231','lbl_answer_2','1','0','Answer 2','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10229','lbl_answer_1','1','0','Answer 1','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11002','lbl_findstore_loc','1','0','City and State, or Zip Code','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11008','lbl_regview_added','1','0','ADDED','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11009','lbl_regview_browse_and_add_gifts','1','0','BROWSE & ADD GIFTS','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11010','lbl_regview_complete_your_registry','1','0','Complete your registry!','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11012','lbl_regview_gift_ideas','1','0','gift ideas','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11013','lbl_regview_numOfGiftsPerGuestCalc','1','0','3','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11014','lbl_regview_purchased','1','0','PURCHASED','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11015','lbl_regview_registry_id','1','0','Registry ID:','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11016','lbl_regview_so_guests_can_view','1','0','so guests can view and purchase gifts','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11018','lbl_regview_visibility','1','0','Visibility:','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11019','lbl_regview_you_have_plenty','1','0','you have plenty of gifts for you guests to choose from.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11020','lbl_regview_your_registry','1','0','YOUR REGISTRY','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11022','lbl_shipping_date_for_packNHold','1','0','Shipping Date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11023','lbl_shipping_promotin_msg_threshold','1','0','0','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11024','lbl_free_shipping_you_mobile','1','0','You are ','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11025','lbl_free_shipping_away_mobile','1','0',' away from Free Shipping','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10433','lbl_edit_favorite_store','1','0','Edit Favorite Store','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11001','lbl_reg_ph_collegeName','1','0','College Name','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11028','lbl_order_summary','1','0','order summary','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11029','lbl_src_reg_private_note','1','0','Don''t worry, you''ll still be able to make your registry private','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11030','lbl_src_college','','0','College/University','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11031','lbl_src_shippingAddr_required','','0','Please enter your Shipping Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11032','lbl_src_other','1','0','Other','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11033','lbl_dsl_alt_modal_head_cart','1','0','Please enter information below to add this item to your cart','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10691','lbl_wrong_password_thrice','1','0','The answer’s you provided to the selected challenge question’s does not match what we have on file. An email has been sent to you with instructions to reset your password.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11034','lbl_ltl_dsl_alt_modal_head_cart','1','0','Please enter information below to add this item to your cart','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11042','lbl_write_the_review_tbs','1','0','Write the first review','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('DC2bbLTA2870001','lbl_src_baby_name_placeholder','1','0','Maiden Name','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11045','lbl_src_nursery_theme_placeholder','1','0','Nursery Decor','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11046','lbl_reg_Address','1','0','address','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11035','lbl_regview_expectedDate','','0','Expected Date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11036','lbl_regview_txtPublicPrivate','','0','Public vs Private','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11037','lbl_regview_txtPublicPrivateDetails','','0','Public means your registry is public and searchable.  Private means only you can see your registry.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11038','lbl_regview_txtItsasurprise','','0','It''s a surprise','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11039','lbl_regview_txtItsaGirl','','0','It''s a Girl','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11040','lbl_regview_txtItsaBoy','','0','It''s a Boy','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11041','lbl_regview_txtGender','','0','Gender','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11017','lbl_regview_the_big_day','','0','The Big Day','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11051','lbl_src_shipping_addr_ph','1','0','Shipping Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11052','lbl_src_maiden_name_ph','1','0','Maiden Name','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11053','lbl_src_last_name_ph','1','0','Last Name','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11054','lbl_src_guest_no_ph','1','0','Number Of Guests','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11055','lbl_src_futureship_addr_ph','1','0','Future Shipping Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11056','lbl_src_full_name_ph','1','0','Full Name (FirstName, Last Name)','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11057','lbl_src_first_name_ph','1','0','First Name','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11058','lbl_src_email_ph','1','0','Email Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11059','lbl_src_coreg_full_name_ph','1','0','Full Name (First Name, Last Name)','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11060','lbl_src_contact_addr_ph','1','0','Contact Address','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11061','lbl_src_confirm_pwd_ph','1','0','Confirm Password','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11062','lbl_src_college_ph','1','0','College/University','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11063','lbl_coreg_email_ph','1','0','Email (Optional)','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11068','lbl_regview_invite_now_but','','0','INVITE NOW','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11069','lbl_regview_your_friends_and_fam','','0','Your friends and family know you want best. get helpful advice on what to register for','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11070','lbl_regview_get_recommen_friends_fam','','0','get recommendations from friends & Family','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11071','lbl_regview_share_your_registry','','0','share your registry','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11072','lbl_regview_wondering_how_many','','0','Wondering how many gifts you should have on your registry? ','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11073','lbl_regview_numOfGiftsPerGuestCalc','1','0','3','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11074','lbl_regview_numOfGiftsPerGuestCalc','','0','3','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11075','lbl_regview_completion_msg_no_guests','','0','Please enter the number of guests that will be attending your event so that we can help you register for enough of the right stuff.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10578','lbl_you_left_off','','','where you left off!','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11078','lbl_regview_shop_this_look','','0','shop this look','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11079','lbl_regview_kickstarters','1','0','Kickstarters','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11081','lbl_registry_completion_msg_public','1','0','<p>Based on <span class="guestCount">$guestCount</span> wedding guests, you have enough gifts for <span class="guestCount">$percentage</span> of your guests</p>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11082','lbl_registry_completion_msg_private','1','0','<p>Wondering how many gifts you should have on your registry? <a href="#" class="editRegInfo">complete your profile</p>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11080','lbl_submit_reg_search','1','0','Submit your registry search','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA10575','lbl_Multi_ship','1','0','Multiship','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11083','lbl_mob_search_filter_by','1','0','Filter By','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11084','lbl_mob_facets_selected_msg','1','0','1 Selected','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11085','lbl_change_your_fav_store','1','0','change your favorite store','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11086','lbl_reg_go_back','1','0','go back to my registries','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,LABEL_VALUE,CHANNEL) values ('BBBPSI8LTA11093','lbl_view_detail','1','0','View Details','DesktopWeb');
----  insert textArea : BBB_CORE_PRV.BBB_LBL_TXT
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11092','txt_registry_creation_hooray_oth','1','1','other !','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11077','txt_co_reg_modal_title','1','1','<h3>Co-Registrant Email Confirmation</h3>','DesktopWeb');

DECLARE
  str varchar2(32767);
BEGIN
  str := '<div class="bgColorS borderS clearfloats marginBottom12 consistentPaddingOnlyF">
                	<div class="row-fluid mwHeight30 marginBottom12">
                        <div class="span2 pr">
                            <span class="pa box_icon radius50 bgColorT inlineBlock iconLock fillColI marginLeft10"><svg height="18" width="18" viewBox="0 0 32 32"><path d="M22 16 L22 12 A6 6 0 0 0 10 12 L10 16 z M4 16 L6 16 L6 12 A10 10 0 0 1 26 12 L26 16 L28 16 L28 30 L4 30 z"/></svg></span>
                        </div>
                        <div class="span10">
                            <div class="txtF3 colorA lineHeightB paddingTopD">Why make my registry public</div>
                        </div>
                    </div>
                	<div class="row-fluid">
                        <div class="span2 pr">
                            <span class="pa box_icon_smallB radius50 bgColorT inlineBlock iconCheckSmall fillColI marginLeft30">-</span>
                        </div>
                        
                        <div class="span10">
                            <div class="txtCC lineHeightB">Allows guests to find you via search</div>
                        </div>
                    </div>
                	<div class="row-fluid">
                        <div class="span2 pr">
                            <span class="pa box_icon_smallB radius50 bgColorT inlineBlock iconCheckSmall fillColI marginLeft30">-</span>
                        </div>
                        <div class="span10">
                            <div class="txtCC lineHeightB">Guests can view and purchase gifts from your registry</div>
                        </div>
                    </div>
                	<div class="row-fluid">
                        <div class="span2 pr">
                            <span class="pa box_icon_smallB radius50 bgColorT inlineBlock iconCheckSmall fillColI marginLeft30"></span>
                        </div>
                        <div class="span10">
                            <div class="txtCC lineHeightB">Share your registry with friends & Family</div>
                        </div>
                    </div>
                	<div class="row-fluid">
                        <div class="span2 pr">
                            <span class="pa box_icon_smallB radius50 bgColorT inlineBlock iconCheckSmall fillColI marginLeft30"></span>
                        </div>
                        <div class="span10">
                            <div class="txtCC lineHeightB">Lorem ipsum dolor amet</div>
                        </div>
                    </div>
                </div>';
 INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11094','lbl_regview_updateFormBottomMsg_textArea','1','1',str,'MobileWeb');
END;
/

INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11088','txt_registry_creation_hooray_hsw','1','1','House warming !','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('DC1bbLTA2720081','txt_registry_creation_hooray_div','1','1','Wedding Registry !','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11089','txt_registry_creation_hooray_com','1','1','Commitment !','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11090','txt_registry_creation_hooray_col','1','1','College !','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11091','txt_registry_creation_hooray_baby','1','1','Baby Registry !','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBAOIDC99999','lbl_date_criterion_message','1','1','Maximum of 21 day range can be selected','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10001','txt_reasons_to_register','1','1','<p>Reasons to Register</p>
 <ul class="infoContentList">
	<li><span aria-hidden="true">H</span>Expert Registry Consultants</li>
	<li><span aria-hidden="true">H</span>10% off on Registry Items Not Purchased</li>
	<li>
		<span aria-hidden="true">H</span><a href="/store/registry/howbookapp/?icid=registrylanding2_Promoarea5">Howbook + App</a></li>
	<li><span aria-hidden="true">H</span>Hassle Free Returns</li>
	<li><a href="/store/registry/RegistryFeatures" >Learn more</a></li>
</ul>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10016','txt_ship_method_not_supported_rlp','1','1','$ShippingMethodDesc service is no longer available at this item.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10018','txt_reg_dsl_tool_tip','1','1','<div class="dslToolTip"   id="dslToolTipInfo">
<span class="personalizationTypeMthd">Delivery Charge</span>
<span>Lorem ipsum dolor sit amet.Lorem ipsum dolor sit amet.</span>
<span class="personalizationTypeMthd">Assembly Charge</span>
<span>Lorem ipsum dolor sit amet.Lorem ipsum dolor sit amet.</span>
</div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10032','txt_add_info_ltl','1','1','For truck delivery items to be added to your registry, an alternate phone number must be provided.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10038','txt_cart_my_offers_description','1','1','<p>Here''s  where you''ll find your offers conveniently organized for you. Upload coupons into My Offer so you can access them any- where. Learn more about My Offers</p>
					<p><span>Note:</span> Offer will not be accessible after expiration.</p>','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10040','txt_ship_method_not_supported_rlp_mob','1','1','The requested delivery service is no longer available at this item. Please update service level in checkout.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10054','txt_dsl_not_available','1','1','No delivery options are currently available for this item','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10055','txt_no_dsl_available','1','1','No delivery options are currently available for this item','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10059','txt_ltl_shipping_not_available','1','1','Truck Delivery items cannot be shipped to a PO Box','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10098','txt_mob_ltl_shipping_not_available','1','1','Truck Delivery items cannot be shipped to a PO Box','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10026','txt_free_shipping_you_mobile','1','1','You are ','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10027','txt_free_shipping_away_mobile','1','1',' away from Free Shipping','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10056','txt_congrats_free_ship_msg','1','1','Your order qualifies for Free Standard Shipping!','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10157','txt_spc_creditcard_save_to_account','1','1','Save Credit Card to your Account','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10160','txt_spc_creditcard_expiry_mandatory','1','1','Expiry Date is required','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10161','txt_spc_creditcard_confirm_name','1','1','Please confirm name on billing address matches the credit card.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10162','txt_spc_creditcard_enter_expiry','1','1','Please enter a valid expiration date','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10242','txt_omniture_boosting_success_body','1','1','<p>Report Processing has been completed successfully for Report ID : $ReportID</p>
<p>Number of Records Inserted For this Report : $RecordsUpdated</p>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10246','txt_email_temp_inv_success','1','1','<p>Loaded coherance cache successfully</p>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10247','txt_email_temp_inv_failure','1','1','<p>Error while loading coherance cache or invalidation</p>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10248','txt_email_temp_inv_scheduler','1','1','""<p><span style=""""color:#2F4F4F"""">Hi Team,&nbsp;</span></p>
<p><span style=""""color:#2F4F4F"""">Invalidate Repository Cache and Loading Coherance cache status are as follows:</span></p>
<p><br /><span style=""""color:#2F4F4F"""">$content</span></p>
<p>&nbsp;</p>
<p><br /><span style=""""color:#2F4F4F"""">Regards,<br />PSI8 Team</span><br /><a href=""""http://communities.bentley.com/help/f/9549/t/62686"""" style=""""box-sizing: border-box; color: rgb(0, 153, 220); text-decoration: none; transition: color 0.1s; outline: 0px; background: transparent;""""><span style=""""color:#2F4F4F"""">[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]</span></a></p>""','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10249','txt_add_info_ltl_cart','1','1','Please add information below to add this item to your cart.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10257','txt_omniture_purge_failure_body','1','1','Production | Omniture boosted products data processing failed for concept : $concept','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10258','txt_omniture_purge_failure_mssg','1','1','Production | Omniture boosted products data processing failed for concept : $concept','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10244','txt_omniture_boosting_success_mssg','1','1','Report Processing Finished Successfully for Concept : $concept','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10245','txt_omniture_boosting_failure_mssg','1','1','Report Processing Failed for Concept : $concept','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10261','txt_manage_reg','1','1','Easily find and keep track of<br/>  everything you need.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10279','txt_disclaimer_pdp','1','1','Availability is based on the quantity in the store at the time you check our site. Store inventory can quickly change, especially when an item is advertised. Store hours may vary during Holiday Season and Back to College. Please contact the store location for more detailed information','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10298','txt_registry_tools','1','1','<ul id=""regToolsList"">
        <li class=""header"">registry tools</li>
        <li><a title=""Guides &amp; Advice"" href=""/store/registry/GuidesAndAdviceLandingPage"">Guides &amp; Advice</a></li>          
        <li><a href=""/store/bbregistry/BridalBook"" title=""Wedding Registry Book"">Wedding Registry Book</a></li>
        <li><a href=""/store/printCards/printCards.jsp"" title=""Registry Announcement Cards"">Announcement Cards</a></li>
        <li><a title=""Personalized Invitations"" href=""/store/registry/PersonalizedInvitations"">Personalized Invitations</a></li>
        <li class=""separator""></li>
        <li><a title=""Registry Features"" href=""/store/registry/RegistryFeatures"">Registry Features</a></li>
        <li><a title=""Registry Incentives"" href=""/store/registry/RegistryIncentives"">Registry Incentives</a></li>
</ul>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10299','txt_registry_hooray_modal','1','1','put your hooray modal content here.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10307','txt_deactivateRegistry','1','1','Don''t worry, you''ll still be able to make your registry public.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10308','txt_exclusiveEmailOffer','1','1','Yes, add me to the Bed Bath & Beyond email list to receive exclusive <br/> email offers and news, you can unsubscribe at any time.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10309','txt_makeRegistryPublic','1','1','Don''t worry, you''ll still be able to make your registry private.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10262','txt_reasons_to_register_tbs','1','1','<ul>
    <li>Free Registry Announcement Cards</li>
    <li>10% off on Registry Items Not Purchased</li>
    <li>Free Goodie Bag</li>
    <li>Hassle Free Returns</li>
</ul>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10319','txt_omniture_queue_success_mssg','1','1','Report Queued successfully for concept : $concept','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10320','txt_omniture_queue_success_body','1','1','Report with report id $ReportID queued successfully for concept : $concept','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10321','txt_omniture_queue_failure_mssg','1','1','Report Queue has been failed for concept : $concept','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10322','txt_omniture_queue_failure_body','1','1','<p>Report was not Queued for concept : $concept</p>
<p>Below Exception Occurred :</p>
<p>$exceptionDetails</p>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10243','txt_omniture_boosting_failure_body','1','1','<p>Report Processing has been failed for Report ID : $ReportID</p>
<p>Below Exception Occurred :</p>
<p>$exceptionDetails</p>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10300','txt_omniture_archival_failure_mssg','1','1','Archival of Records has been failed for Concept : $concept','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10301','txt_omniture_archival_failure_body','1','1','<p>Archival of Records has been failed for Concept : $concept.</p>
<p>Below Exception Occurred:</p>
<p>$exceptionDetails</p>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10323','txt_free_shipping_diff','1','1','You are $shippingDifference away from Free Shipping','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10324','txt_registry_hooray_modal_wedding','1','1','put your hooray modal content here.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10325','txt_registry_hooray_modal_baby','1','1','put your hooray modal content here.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10326','txt_registry_hooray_modal_birthday','1','1','put your hooray modal content here.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10327','txt_registry_hooray_modal_retirement','1','1','put your hooray modal content here.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10328','txt_registry_hooray_modal_anniversary','1','1','put your hooray modal content here.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10329','txt_registry_hooray_modal_housewarming','1','1','put your hooray modal content here.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10330','txt_registry_hooray_modal_commitment','1','1','put your hooray modal content here.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10331','txt_registry_hooray_modal_college','1','1','put your hooray modal content here.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10332','txt_registry_hooray_modal_other','1','1','put your hooray modal content here.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10334','txt_registry_quotes_wedding_public','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Tell everyone you''re #HappilyRegistered!</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10335','txt_registry_quotes_baby_public','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to share your registry with friends & family</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10336','txt_registry_quotes_birthday_public','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to share your registry with friends & family</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10337','txt_registry_quotes_retirement_public','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to share your registry with friends & family</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10338','txt_registry_quotes_anniversary_public','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Tell everyone you''re #HappilyRegistered!</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10339','txt_registry_quotes_housewarming_public','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Tell everyone you''re #HappilyRegistered!</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10340','txt_registry_quotes_commitment_public','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Tell everyone you''re #HappilyRegistered!</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10341','txt_registry_quotes_college_public','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to share your registry with friends & family</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10342','txt_registry_quotes_other_public','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to share your registry with friends & family</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10343','txt_registry_quotes_wedding_private','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to complete your profile so guests can spoil you</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10344','txt_registry_quotes_baby_private','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to complete your profile so guests buy you gifts</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10345','txt_registry_quotes_birthday_private','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to complete your profile so guests buy you gifts</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10346','txt_registry_quotes_retirement_private','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to complete your profile so guests buy you gifts</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10347','txt_registry_quotes_anniversary_private','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to complete your profile so guests can spoil you</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10348','txt_registry_quotes_housewarming_private','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to complete your profile so guests can spoil you</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10349','txt_registry_quotes_commitment_private','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to complete your profile so guests can spoil you</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10350','txt_registry_quotes_college_private','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to complete your profile so guests buy you gifts</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10353','txt_registry_quotes_other_private','1','1','<div id="regHeaderQuotes"> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <p style="font-family: ''ITCNewBaskervilleRomanItalic'';font-style: italic;font-size: 20px;"> Make sure to complete your profile so guests buy you gifts</p> <span></span> </div> <div style="text-align:center;padding:40px 0px;box-sizing:border-box;"> <h3>love your kitchen</h3> <p>Equip your kitchen with quality cookware, cultery, and electric helpers.</p> <a href="#" class="button-Med btnSecondary" style="float:none;">VIEW COLLECTION</a> </div> </div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10413','notifySliderMessageForCart','1','1','Are you sure you want to add this item to your registry or cart?','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10414','notifySliderMessageN','1','1','The product you selected has limited availability.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10417','notifySliderMessageD','1','1','The item you selected has been discontinued.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10426','notifySliderMessageForAddItem','1','1','Are you sure you want to add item?','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10644','txt_omniture_general_failure_mssg','1','1','Some exception occurred while invoking Queue or Get API for concept : $concept','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10645','txt_omniture_general_failure_body','1','1','<p>Below exception occurred while invoking Queue or Get API for concept : $concept</p>
<p>$exceptionDetails</p>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10639','lbl_search_left_nav_accordion_expand','1','1','','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10649','lbl_search_left_nav_accordion_collapse','1','1','','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10893','txt_cart_what_this_mean_content','1','1','<p>You have reached our threshold for maximum surcharge costs. You will not pay any truck delivery surcharges over our maximum threshold.</p>','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10902','advanced_order_search_min_req','1','1','<h3>Minimum Search Combinations</h3><br>First Name (At least first character), Last Name (At least first two characters) AND Start Date-End Date<br>First Name (At least first character), Last Name (At least first two characters) AND Store #<br>Store # and Start Date-End Date<br>Registry #<p></p>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10960','txt_registry_creation_hooray_div','1','1','Hooray modal HTML goes here...','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10965','lbl_rating_out_of_five_stars','1','1','','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10967','txt_update_modal_make_public_benefit_msg','1','1','<ul id="guidelinesToMakeRegPub">
  <li id="yMakeRegPub">
   <p class="yMakeRegPubIcon"></p><p class="yMakeRegPubText">why make my registry public</p>
  </li>
  <li class="glForPub">
   <p class="glIconForPub"></p><p class="glTextForPub noMar">Allows guests to find you via search</p>
  </li>
  <li class="glForPub">
   <p class="glIconForPub"></p><p class="glTextForPub">Guests can view and purchase gifts from your registry</p>
  </li>
  <li class="glForPub">
   <p class="glIconForPub"></p><p class="glTextForPub">Share your registry with friends &amp; family</p>
  </li>
</ul>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA109890','txt_registry_favorites_tab_content_anniversary','1','1','default registry favorites content','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA109891','txt_registry_favorites_tab_content_baby','1','1','default registry favorites content','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA109892','txt_registry_favorites_tab_content_birthday','1','1','default registry favorites content','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA109893','txt_registry_favorites_tab_content_college','1','1','default registry favorites content','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA109894','txt_registry_favorites_tab_content_commitment','1','1','default registry favorites content','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA109895','txt_registry_favorites_tab_content_housewarming','1','1','default registry favorites content','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA109896','txt_registry_favorites_tab_content_other','1','1','default registry favorites content','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA109897','txt_registry_favorites_tab_content_retirement','1','1','default registry favorites content','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA109898','txt_registry_favorites_tab_content_wedding','1','1','default registry favorites content','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBMAYPS16LTA10003','lbl_footer_logo_bedbathbeyond','1','1','<div class="fl" id="otherSitesTitle"><p>Visit our other sites:</p></div>
<div class="fl" id="logoBb"><a href="//www.buybuybaby.com" target="_blank" title="Buy Buy Baby">Buy Buy Baby</a></div>
<div class="fl" id="logoHarmon"><a href="http://www.harmondiscount.com" target="blank" title="Harmon Face Values">Harmon Face Values</a></div>
<div class="fl" id="logoCpwm"><a href="http://www.worldmarket.com/" target="blank" title="Cost Plus World Market">Cost Plus World Market</a></div>
<div class="fl" id="logoBy"><a href="/store/registry/PersonalizedInvitations" target="_blank" title="Bed Bath & Beyond Personalized Invitations">Bed Bath & Beyond Personalized Invitations</a></div>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11011','lbl_regview_giftCalculationB','1','1','you have plenty of gifts for you guests to choose from.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11021','lbl_regview_giftCalculationA','1','1','Based upon <span id="numOfguests" class="txtW2"><c:out value="${registryDetails.registryResVO.registryVO.event.guestCount}"/></span> wedding guests, you have enough gifts for <span id="giftGuestsPercentageDisplay" class="txtW2"></span>% of your guests','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11026','txt_ship_promp_static_msg','1','1','Free shipping available at','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA10066','txt_moved_to_sfl_plus_link','1','1','has moved to Saved Items. <a class="view-saved-items"  href="javascript:void(0);">View Saved Items</a>','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11047E','advanced_order_search_min_req','1','1','<h3>Minimum Search Combinations</h3>First Name (At least first character), Last Name (At least first two characters) AND Start Date-End Date<br>First Name (At least first character), Last Name (At least first two characters) AND Store #<br>Store # and Start Date-End Date<br>Registry #<p></p>','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11043','lbl_regview_updateFormTopMsg_textArea','','1','<div style="overflow:auto" class="bgColorS borderS clearfloats marginBottom12 consistentPaddingOnlyF"><div class="span2 pr"><span class="pa centerLockIcon box_icon radius50 bgColorT inlineBlock iconLock fillColI"><svg height=18 width=18 viewBox="0 0 32 32"xmlns=http://www.w3.org/2000/svg><path d="M22 16 L22 12 A6 6 0 0 0 10 12 L10 16 z M4 16 L6 16 L6 12 A10 10 0 0 1 26 12 L26 16 L28 16 L28 30 L4 30 z"/></svg></span></div><div class=span10><div class="txtF3 colorA">your registry is currently private</div><div class="txtCC lineHeightB">Complete and save your registry so guests can view and purchase gifts</div></div></div>','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11044','lbl_regview_updateFormTopMsg_textArea','','1','<div style="overflow:auto" class="bgColorS borderS clearfloats marginBottom12 consistentPaddingOnlyF"><div class="span2 pr"><span class="pa centerLockIcon box_icon radius50 bgColorT inlineBlock iconLock fillColI"><svg height=18 width=18 viewBox="0 0 32 32"xmlns=http://www.w3.org/2000/svg><path d="M22 16 L22 12 A6 6 0 0 0 10 12 L10 16 z M4 16 L6 16 L6 12 A10 10 0 0 1 26 12 L26 16 L28 16 L28 30 L4 30 z"/></svg></span></div><div class=span10><div class="txtF3 colorA">your registry is currently private</div><div class="txtCC lineHeightB">Complete and save your registry so guests can view and purchase gifts</div></div></div>','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11047','txt_footer_twitter_share','','1','<a href="https://twitter.com/BedBathBeyond" class="icon2"><span class="s_twitter box_icon fl"></span></a>','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11048','txt_footer_pinterest_share','','1','<a href="http://pinterest.com/bedbathbeyond/" class="icon2"><span class="s_pintrist box_icon fl"></span></a>','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11049','txt_footer_facebook_share','','1','<a href="https://www.facebook.com/BedBathAndBeyond" class="icon2"><span class="s_facebook box_icon fl"></span></a>','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11050','lbl_regview_completion_msg_based_upon_textArea','','1','<div id="giftCalculationA" class="span12 textAlignCenter txtCC colorA marginTopB paddingA displayBlock">Based upon <span id="numOfguests" class="txtW2"></span> wedding guests, (regview) you have enough gifts for <span id="giftGuestsPercentageDisplay" class="txtW2"></span>% of your guests </div>','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11064','txt_footer_twitter_share','1','1','<a href="https://twitter.com/BedBathBeyond" class="icon2"><span class="s_twitter box_icon fl"></span></a>','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11065','txt_footer_pinterest_share','1','1','<a href="http://pinterest.com/bedbathbeyond/" class="icon2"><span class="s_pintrist box_icon fl"></span></a>','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11066','txt_footer_facebook_share','1','1','<a href="https://www.facebook.com/BedBathAndBeyond" class="icon2"><span class="s_facebook box_icon fl"></span></a>','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11067','lbl_regview_completion_msg_based_upon_textArea','1','1','<div id="giftCalculationA" class="span12 textAlignCenter txtCC colorA marginTopB paddingA displayBlock">Based upon <span id="numOfguests" class="txtW2"></span> wedding guests, (regview) you have enough gifts for <span id="giftGuestsPercentageDisplay" class="txtW2"></span>% of your guests </div>','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('BBBPSI8LTA11076','lbl_regview_completion_msg_based_upon_textArea','','1','<div id="giftCalculationA" class="span12 textAlignCenter txtCC colorA marginTopB paddingA displayBlock">Based upon <span id="numOfguests" class="txtW2"></span> wedding guests, (regview) you have enough gifts for <span id="giftGuestsPercentageDisplay" class="txtW2"></span>% of your guests </div>','MobileWeb');

INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,TEXTAREA_VALUE,CHANNEL) values ('DC1bbAOI999999','advanced_order_search_min_req','1','1','<h3>Minimum Search Combinations (Maximum of resultSetSize results will be displayed)</h3><br>First Name (At least first character), Last Name (At least first two characters) AND Start Date-End Date<br>First Name (At least first character), Last Name (At least first two characters) AND Store #<br>Store # and Start Date-End Date<br>Registry #<p></p>','DesktopWeb');
---- insert error message : BBB_CORE_PRV.BBB_LBL_TXT
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10010','err_profile_invalid_reset_password_token','1','2','Invalid/Expired token. Please enter a valid token sent to registered email address.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10011','err_profile_empty_reset_password_token','1','2','No token supplied. Please enter a valid token sent to registered email address.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10024','err_js_delivery_sel_warning_registry','1','2','Please select a freight delivery option to add this item to your registry.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10077','err_mobile_cart_update','1','2','Some thing went wrong while updating  the cart. Please try later','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10078','err_mobile_cart_item_remove','1','2','Some thing went wrong while removing the item from cart. Please try later','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10079','err_mobile_move_item_to_sfl','1','2','Something went wrong while moving item to list. Please try again later.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10080','err_mobile_move_sfl_item_to_cart','1','2','Some thing went wrong while while moving SFL item to cart. Please try later','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10081','err_mobile_change_pickup_to_online','1','2','Some thing went wrong while changing Store Pick UP item to Online . Please try later','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10082','err_apply_coupon_to_order','1','2','Some thing went wrong applying coupon to order. Please try later','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10083','ERR_CHECKOUT_FETCH_COUPONS','1','2','Error occured while fetching coupons. Please try after some time.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10085','ERR_CART_BUY_OFF','1','2','Error occured while associating item to registry.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10099','err_setting_email_signup_in_order','1','2','Error occured while setting email sign up flag for offers.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10100','err_service_invocation','1','2','ERROR: Service invocation has failed. <br> Please contact customer support.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10129','err_remove_envoy','1','2','Offers that are applied on order will be removed on Envoy Page.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10165','err_spc_paypal_unavailable','1','2','PayPal is not available. Select another form of payment','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10166','ERROR_EMPTY_PROFILE_ID','1','2','Profile Id cannot be empty','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10167','ERROR_LST_NM_INVALID','1','2','Last name has invalid characters','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10168','ERROR_LST_NM_EMPTY','1','2','Last name was not entered','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10169','ERROR_LST_NM_TOO_LONG','1','2','Last name too long','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10170','ERROR_FST_NM_INVALID','1','2','First name has invalid characters','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10171','ERROR_FST_NM_EMPTY','1','2','First name was not entered','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10172','ERROR_FST_NM_TOO_LONG','1','2','First name is too long','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10173','ERROR_PHONE_INVALID_CHARS','1','2','Phone has invalid characters','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10174','ERROR_PHONE_INVALID','1','2','Invalid phone number','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10175','ERROR_MOBILE_INVALID_CHARS','1','2','Eve phone has invalid characters','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10176','ERROR_MOBILE_INVALID','1','2','Invalid eve phone number','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10177','ERROR_EMAIL_INVALID_CHARS','1','2','Email has invalid characters','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10178','ERROR_EMAIL_EMPTY','1','2','Email was not entered','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10179','ERROR_EMAIL_INVALID','1','2','Invalid email','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10180','err_ss_mob_name_validation','1','2','Please enter chars only','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10181','err_ss_mob_paypal_remove_msg','1','2','PayPal will be removed','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10182','err_ss_mob_gift_card_remove_msg','1','2','Gift Cards will be removed','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10183','err_ss_mob_credit_card_remove_msg','1','2','Credit Card will be removed','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10192','err_challenge_answer_not_matched','1','2','Incorrect Security Answer','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10252','error_reservenow_dom_call_error','1','2','We''re sorry, an error has occurred. Please try again later or call 1-800-GO-BEYOND to place order.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10253','error_reservenow_fetching_inventory','1','2','Error while Inventory Fetching','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10254','error_reservenow_no_inventory','1','2','Items cannot be added to cart because Store is out of stock with this item, please try ordering in a different store.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10263','ERROR_SFL_MAX_REACHED','1','2','You have reached your limit for save for later. Remove items to continue.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10264','ERROR_SFL_MAX_REACHED','1','2','You have reached your limit for save for later. Remove items to continue.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10267','ERROR_FROM_LOCAL_INVENTORY','1','2','There is some error in fetching avaibility from your preferred store ','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10268','ERROR_IN_VIEW_ALL_STORES','1','2','The Server is unavailable at this moment, please come back later','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10269','ERROR_IN_VIEW_FAV_STORES','1','2','There is some error in fetching avaibility from your preferred store.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10270','INVENTORY_NOT_AVAILABLE','1','2','Not available for pickup within','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10317','err_js_errPOBoxAddNotAllowedForLTL','1','2','PO Box address is not allowed for LTL items','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10255','error_reservenow_localstore_repos','1','2','Items cannot be added to cart because Store is out of stock with this item, please try ordering in a different store.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10318','error_from_DOM','1','2','There was an error in adding item from this store','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10409','err_reg_required','1','2','please enter required field','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10410','err_reg_email_required','1','2','email address is required','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10444','RESERVE_ONLINE_NOT_AVAILABLE','1','2','Reserve Online is currently not available at this location','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10445','ERROR_INSUFFICIENT_MEMORY','1','2','InSufficient available inventory, please reduce quantity requested','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10458','ERROR_IN_VIEW_ALL_STORES','','2','The Server is unavailable at this moment, please come back later','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10459','ERROR_FROM_LOCAL_INVENTORY','1','2','There is some error in fetching avaibility from your preferred store ','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10460','INVENTORY_NOT_AVAILABLE','1','2','Not available for pickup within','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10642','err_reset_link','1','2','This link has expired. Please try resetting your password again. Or Incorrect Login ID entered','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10643','err_reset_email','1','2','Please make sure the email-id entered is correct','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10961','ERROR_CART_MAX_REACHED','1','2',' You have reached your limit for Add to Cart. Remove items to continue.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10962','ERROR_CART_MAX_REACHED','1','2',' You have reached your limit for Add to Cart. Remove items to continue.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10963','err_add_to_cart_max_limit','1','2','Error occured while adding item(s) to the Cart','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10964','err_saved_items_max_limit','1','2','Error occured while adding item(s) to Saved Items','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10985','error_reservenow_dom_call_error','1','2','We''re sorry, an error has occurred. Please try again later or call 1-800-GO-BEYOND to place order.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10986','error_reservenow_no_inventory','1','2','Items cannot be added to cart because Store is out of stock with this item, please try ordering in a different store','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10987','error_reservenow_fetching_inventory','1','2','Error while Inventory Fetching','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10988','ERROR_INSUFFICIENT_MEMORY','1','2','InSufficient available inventory, please reduce quantity requested','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109899','err_create_co_reg_last_name_invalid','1','2','Co-Registrant Last Name must contain letters, apostrophes, hyphens, and spaces only.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109900','err_create_co_reg_first_name_invalid','1','2','Co-Registrant First Name must contain letters, apostrophes, hyphens, and spaces only.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10989','error_from_DOM','1','2','There was an error in adding item from this store','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109901','err_create_reg_zip_invalid_shipping','1','2','In Shipping Address, Please specify a valid zip or postal code.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109902','err_create_reg_zip_invalid_future','1','2','In Future Shipping Address, Please specify a valid zip or postal code.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109903','err_create_reg_zip_invalid_contact','1','2','In Contact Address, Please specify a valid zip or postal code.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109904','err_create_reg_state_invalid_contact','1','2','In Contact Address, Please Enter a valid state.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109905','err_create_reg_city_invalid_shipping','1','2','In Shipping Address, City must be between <min char> and <max char> characters.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109906','err_create_reg_city_invalid_future','1','2','In Future Shipping Address, City must be between <min char> and <max char> characters.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109907','err_create_reg_city_invalid_contact','1','2','In Contact Address, City must be between <min char> and <max char> characters.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109908','err_create_reg_address_line2_invalid_shipping','1','2','Shipping Address must be between 1 and 50 characters.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109909','err_create_reg_address_line2_invalid_future','1','2','Future Shipping Address must be between 1 and 50 characters.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109910','err_create_reg_address_line2_invalid_contact','1','2','Contact Address must be between 1 and 50 characters.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109911','err_create_reg_address_line1_invalid_shipping','1','2','Shipping Address must be between 1 and 50 characters.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109912','err_create_reg_address_line1_invalid_future','1','2','Future Shipping Address must be between 1 and 50 characters.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA109913','err_create_reg_address_line1_invalid_contact','1','2','Contact Address must be between 1 and 50 characters.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA10995','txt_spc_creditcard_expiry_date_msg','1','2','','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA11003','err_getUserCoupons_serviceDown','1','2','Unable to load the offers, please try after some time','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA11004','err_createwallet_serviceDown','1','2','Offer registration  is unavailable, please  try after sometime','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA11005','err_couponwallet_errorcode_serviceDown','1','2','Unable to upload coupon, please try after sometime.','MobileWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA11006','err_networkAffiliation_invalid','1','2','NetworkAffilication is invalid. ','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA11007','err_networkAffiliation_empty','1','2','Network_affiliation is empty.','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA11027','err_js_num_greater_than_zero','1','2','Please enter a value greater than or equal to 1','DesktopWeb');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT (LBL_TXT_ID,KEY,LANGUAGE,TEMPLATE_TYPE,ERROR_MSG,CHANNEL) values ('BBBPSI8LTA11095','err_ship_zipcode_restricted_for_sku_extended','1','2','The entered shipping zip code is restricted for the item added to cart. Please remove restricted item from cart or change shipping address to place an order.','MobileWeb');

--- update label translation : BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS

UPDATE BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS SET TEXTAREA_VALUE = '<div class="marPromoBox_LR regLandPromoBoxBorder"> <a href="/m/kickStarters?icid=homepage_Promoarea1-homepage"> <img src="/_assets/mobileAssets/global/images/bbb_registry_kickstarter.png" alt="Registry Kickstarter"> </a></div>' WHERE TRANSLATION_ID = 'PSI7TRN0003';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS SET TEXTAREA_VALUE = '<div class="pickUpInStoreMsgBOPUS">                <h3>In-Store Pickup - Reserve Online Pay in Store.</h3><a href="#" class="BopusMsgMore" aria-hidden=''true''>Learn More</a><a href="#" class="BopusMsgLess hidden" aria-hidden=''false''>Hide</a>                <div class="BopusMsg padTop_10 hidden">                                                             
                                <ol>
                                                <li>1. Select "Find In Store" to find out pick up availability. Select your location and "Add to Cart".</li>
                                                <li>2. Go to Cart and check out with your credit card information to <strong>Reserve</strong> your order.</li>
                                                <li>3. Your "Ready for Pick Up" email will arrive within 3 hours. Present this email and <strong>Pay</strong> for your item(s) at pick up.</li>
<li>4. Items will be held for 2 days after you receive your "Ready for Pick Up" email.</li>
                                </ol> 
<hr>
<p><strong>Going off to University and want your items held longer? Learn about Pack & Hold and start your journey. <a href="/store/registry/MovingSolution">View Details</a></strong></p>
<p><strong>Any items reserved online from a registry will not show as purchased until they are picked up and paid for at the store.</strong></p>
</div>                            ' WHERE TRANSLATION_ID = 'PUBbbTT860004';
UPDATE BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS SET TEXTAREA_VALUE = '<div class="pickUpInStoreMsgBOPUS">                <h3>In-Store Pickup - Reserve Online Pay in Store.</h3><a href="#" class="BopusMsgMore" aria-hidden=''true''>Learn More</a><a href="#" class="BopusMsgLess hidden" aria-hidden=''false''>Hide</a>                <div class="BopusMsg padTop_10 hidden">                                                             
                                <ol>
                                                <li>1. Select "Find In Store" to find out pick up availability. Select your location and "Add to Cart".</li>
                                                <li>2. Go to Cart and check out with your credit card information to <strong>Reserve</strong> your order.</li>
                                                <li>3. Your "Ready for Pick Up" email will arrive within 3 hours. Present this email and <strong>Pay</strong> for your item(s) at pick up.</li>
<li>4. Items will be held for 2 days after you receive your "Ready for Pick Up" email.</li>
                                </ol> 
<hr>
<p><strong>Going off to University and want your items held longer? Learn about Pack & Hold and start your journey. <a href="/store/registry/MovingSolution">View Details</a></strong></p>
<p><strong>Any items reserved online from a registry will not show as purchased until they are picked up and paid for at the store.</strong></p>
</div>                            ' WHERE TRANSLATION_ID = 'PUBbbTT760004';


--- insert label translations : BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS


INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS (TRANSLATION_ID,LABEL_VALUE,TEXTAREA_VALUE,ERROR_MSG) values ('PSI8TRN0001','','Yes, add me to the buybuy BABY email list to <br/>receive exclusive email offers and news.','');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS (TRANSLATION_ID,LABEL_VALUE,TEXTAREA_VALUE,ERROR_MSG) values ('PSI8TRN0002','','<ul>
	<li>Friend Referral Program</li>
	<li>Discount on Registry Items Not Purchased</li>
	<li>Free Goodie Bag</li>
	<li>Hassle Free Returns</li>
</ul>','');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS (TRANSLATION_ID,LABEL_VALUE,TEXTAREA_VALUE,ERROR_MSG) values ('BBBPSI8LTT0001','dd/mm/yyyy','','');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS (TRANSLATION_ID,LABEL_VALUE,TEXTAREA_VALUE,ERROR_MSG) values ('BBBPSI8LTT0003','your name','','');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS (TRANSLATION_ID,LABEL_VALUE,TEXTAREA_VALUE,ERROR_MSG) values ('BBBPSI8LTT0002','contact address','','');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS (TRANSLATION_ID,LABEL_VALUE,TEXTAREA_VALUE,ERROR_MSG) values ('BBBMAYPS16LTT10001','','<div class=""fl"" id=""otherSitesTitle""><p>Visit our other sites:</p></div>
<div class=""fl"" id=""logoBb""><a href=""//www.buybuybaby.com"" target=""_blank"" title=""Buy Buy Baby"">Buy Buy Baby</a></div>
<div class=""fl"" id=""logoHarmon""><a href=""http://www.harmondiscount.com"" target=""blank"" title=""Harmon Face Values"">Harmon Face Values</a></div>
','');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS (TRANSLATION_ID,LABEL_VALUE,TEXTAREA_VALUE,ERROR_MSG) values ('BBBMAYPS16LTT10002','','<div class=""fl"" id=""otherSitesTitle""><p>Visit our other sites:</p></div>
<div class=""fl"" id=""logoBy2""><a href=""http://www.bedbathandbeyond.com"" target=""_blank"" title=""Bed Bath & Beyond"">Bed Bath & Beyond</a></div>
<div class=""fl"" id=""logoHarmon""><a href=""http://www.harmondiscount.com"" target=""blank"" title=""Harmon Face Values"">Harmon Face Values</a></div>
<div class=""fl"" id=""logoCpwm""><a href=""http://www.worldmarket.com/"" target=""blank"" title=""Cost Plus World Market"">Cost Plus World Market</a></div>
<div class=""fl"" id=""logoBb2""><a href=""/store/registry/PersonalizedInvitations"" target=""_blank"" title=""buybuy BABY"">buybuy BABY</a></div>','');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS (TRANSLATION_ID,LABEL_VALUE,TEXTAREA_VALUE,ERROR_MSG) values ('BBBPSI8TT1130003','km','','');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS (TRANSLATION_ID,LABEL_VALUE,TEXTAREA_VALUE,ERROR_MSG) values ('BBBPSI8LTT12345','Province','','');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS (TRANSLATION_ID,LABEL_VALUE,TEXTAREA_VALUE,ERROR_MSG) values ('BBBPSI8LTT0004','University Name','','');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS (TRANSLATION_ID,LABEL_VALUE,TEXTAREA_VALUE,ERROR_MSG) values ('BBBPSI8LTT0005','City and State, or Postal Code','','');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS (TRANSLATION_ID,LABEL_VALUE,TEXTAREA_VALUE,ERROR_MSG) values ('BBBPSI8LT1190002','University','','');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS (TRANSLATION_ID,LABEL_VALUE,TEXTAREA_VALUE,ERROR_MSG) values ('BBBPSI8LT1200001','University Name','','');

--- insert label translation relationship : BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS

INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS (LBL_TXT_ID,SITE_ID,TRANSLATION_ID) values ('BBBPSI8LTA10308','TBS_BuyBuyBaby','PSI8TRN0001');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS (LBL_TXT_ID,SITE_ID,TRANSLATION_ID) values ('BBBPSI8LTA10262','TBS_BuyBuyBaby','PSI8TRN0001');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS (LBL_TXT_ID,SITE_ID,TRANSLATION_ID) values ('BBBPSI8LTA10392','BedBathCanada','BBBPSI8LTT0001');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS (LBL_TXT_ID,SITE_ID,TRANSLATION_ID) values ('BBBPSI8LTA10361','BuyBuyBaby','BBBPSI8LTT0002');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS (LBL_TXT_ID,SITE_ID,TRANSLATION_ID) values ('BBBPSI8LTA10403','BuyBuyBaby','BBBPSI8LTT0003');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS (LBL_TXT_ID,SITE_ID,TRANSLATION_ID) values ('BBBMAYPS16LTA10003','BuyBuyBaby','BBBMAYPS16LTT10001');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS (LBL_TXT_ID,SITE_ID,TRANSLATION_ID) values ('BBBMAYPS16LTA10003','BedBathCanada','BBBMAYPS16LTT10002');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS (LBL_TXT_ID,SITE_ID,TRANSLATION_ID) values ('BBBPSI8LTA10443','BedBathCanada','BBBPSI8TT1130003');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS (LBL_TXT_ID,SITE_ID,TRANSLATION_ID) values ('600182','TBS_BedBathCanada','BBBPSI8LTT12345');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS (LBL_TXT_ID,SITE_ID,TRANSLATION_ID) values ('BBBPSI8LTA11002','BedBathCanada','BBBPSI8LTT0005');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS (LBL_TXT_ID,SITE_ID,TRANSLATION_ID) values ('601204','TBS_BedBathCanada','BBBPSI8LT1190002');
INSERT INTO BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS (LBL_TXT_ID,SITE_ID,TRANSLATION_ID) values ('BBBPSI8LTA11001','TBS_BedBathCanada','BBBPSI8LT1200001');

---- insert challenge questions : BBB_CORE_PRV.BBB_CHALLENGE_QUESTIONS


INSERT INTO BBB_CORE_PRV.BBB_CHALLENGE_QUESTIONS (CHALLENGE_QUESTION_ID,QUESTION_TYPE,CHALLENGE_QUESTION) values ('1_challengeQuestion1','1','What is the name of your favorite teacher?');
INSERT INTO BBB_CORE_PRV.BBB_CHALLENGE_QUESTIONS (CHALLENGE_QUESTION_ID,QUESTION_TYPE,CHALLENGE_QUESTION) values ('1_challengeQuestion2','1','What is the name of your first crush?');
INSERT INTO BBB_CORE_PRV.BBB_CHALLENGE_QUESTIONS (CHALLENGE_QUESTION_ID,QUESTION_TYPE,CHALLENGE_QUESTION) values ('1_challengeQuestion3','1','What is the name of your first school?');
INSERT INTO BBB_CORE_PRV.BBB_CHALLENGE_QUESTIONS (CHALLENGE_QUESTION_ID,QUESTION_TYPE,CHALLENGE_QUESTION) values ('1_challengeQuestion4','1','What is your maternal grandmother''s name?');
INSERT INTO BBB_CORE_PRV.BBB_CHALLENGE_QUESTIONS (CHALLENGE_QUESTION_ID,QUESTION_TYPE,CHALLENGE_QUESTION) values ('1_challengeQuestion5','1','What is your mother''s maiden name?');
INSERT INTO BBB_CORE_PRV.BBB_CHALLENGE_QUESTIONS (CHALLENGE_QUESTION_ID,QUESTION_TYPE,CHALLENGE_QUESTION) values ('2_challengeQuestion1','2','What is your nickname?');
INSERT INTO BBB_CORE_PRV.BBB_CHALLENGE_QUESTIONS (CHALLENGE_QUESTION_ID,QUESTION_TYPE,CHALLENGE_QUESTION) values ('2_challengeQuestion2','2','What is your pet''s name?');
INSERT INTO BBB_CORE_PRV.BBB_CHALLENGE_QUESTIONS (CHALLENGE_QUESTION_ID,QUESTION_TYPE,CHALLENGE_QUESTION) values ('2_challengeQuestion3','2','Who was your childhood hero?');
INSERT INTO BBB_CORE_PRV.BBB_CHALLENGE_QUESTIONS (CHALLENGE_QUESTION_ID,QUESTION_TYPE,CHALLENGE_QUESTION) values ('2_challengeQuestion4','2','What is your favorite hobby?');
INSERT INTO BBB_CORE_PRV.BBB_CHALLENGE_QUESTIONS (CHALLENGE_QUESTION_ID,QUESTION_TYPE,CHALLENGE_QUESTION) values ('2_challengeQuestion5','2','What is your favorite television show?');

---- update email template : BBB_CORE_PRV.BBB_EMAIL_TEMPLATE

UPDATE BBB_CORE_PRV.BBB_EMAIL_TEMPLATE SET EMAIL_TYPE='201', 
 EMAIL_FROM='customer.service@bedbathandbeyond.com', 
 EMAIL_SUBJECT='Password Reset Request', 
 EMAIL_BODY='"<!-- // Begin Template Main Content \\ -->
    <tr>
        <td align="left" valign="top">
         <table border="0" cellpadding="4" cellspacing="4" width="100%" style="font-size:14px; line-height:14px; color:#666666;font-family:Arial;">
            <tr>
    <td height="5" style="line-height:5px;">
                </td>
   </tr>
   <tr>
    <td valign="top"><h3 class="resetTextSize" style="padding:0;margin:0;font-family:arial;">Dear frmData_firstName,</h3>
                </td>
   </tr>
            <tr>
    <td valign="top"><p class="resetTextSize" style="margin:0;padding:0;color:#666666;font-family:Arial;font-size:14px;line-height:16px;">You requested a password reset.Please click on the link below to complete the process</p>
                </td>
   </tr>            
            <tr>
    <td valign="top"><a style="color: #273691;" href="account_login_url?token=encoded_token" target="_blank">Reset Password</a>
            </td>
   </tr>             
            <tr>
    <td valign="top"><p class="resetTextSize" style="margin:0;padding:0;color:#666666;font-family:Arial;font-size:14px;line-height:18px;">If you did not request a password reset please email <a style="color: #273691;" href="mailto:customer.service@bedbath.com">customer.service@bedbath.com</a> or contact customer service at 1-800-GO-BEYOND&reg; (1-800-462-3966) and we can assist you.</p>
                </td>
   </tr>
   <tr>
    <td valign="top" class="alignCenter">
                <p>Sincerely,<br>
  Customer Service<br>
  Bed Bath &amp; Beyond Inc.<br>
  <a style="color: #273691;" href="mailto:customer.service@bedbath.com">customer.service@bedbath.com</a></p>
</td>
   </tr>   
 </table>        
        </td>
    </tr>
    <!-- // End Template Main Content \\ -->  "', 
 EMAIL_FLAG='ON' 
 WHERE EMAIL_TEMPLATE_ID='100003';
 
---- update email translations : BBB_CORE_PRV.BBB_EMAIL_TRANSLATIONS

UPDATE BBB_CORE_PRV.BBB_EMAIL_TRANSLATIONS SET SITE_ID='BuyBuyBaby', 
 LOCALE_ID='en_US', 
 EMAIL_FROM='customer.service@buybuybaby.com', 
 EMAIL_SUBJECT='Password Reset Request', 
 EMAIL_BODY='<!-- // Begin Template Main Content \\ -->
    <tr>
        <td align="left" valign="top">
        <table border="0" cellpadding="4" cellspacing="4" width="100%" style="font-size:14px; line-height:14px; color:#666666;font-family:Arial;">
    <tr>
    <td height="5" style="line-height:5px;">
                </td>
   </tr>
   <tr>
    <td valign="top"><h3 class="resetTextSize" style="padding:0;margin:0;font-family:arial;">Dear frmData_firstName,</h3>
                </td>
   </tr>
            <tr>
    <td valign="top"><p class="resetTextSize" style="margin:0;padding:0;color:#666666;font-family:Arial;font-size:14px;line-height:16px;">You requested a password reset.</p>
                </td>
   </tr>
            <tr>
    <td valign="top"><p class="resetTextSize" style="margin:0;padding:0;color:#666666;font-family:Arial;font-size:14px;line-height:16px;">Please click on the link below to complete the reset password flow.If you have an existing Bed Bath & Beyond account or registry, changing your buybuy BABY password will also apply to your Bed Bath & Beyond account.</p>
                </td>
   </tr>
   <tr>
    <td valign="top"><a style="color: #273691;" href="account_login_url?token=encoded_token" target="_blank">Reset Password</a>       
				  </td>
				  </tr>
            <tr>
    <td valign="top"><p class="resetTextSize" style="margin:0;padding:0;color:#666666;font-family:Arial;font-size:14px;line-height:16px;">If you did not request a password reset please contact customer service at 1-877-3-BUY-BABY(1-877-328-9222) or email <a href="mailto:customer.service@buybuybaby.com">customer.service@buybuybaby.com </a> and we can assist you.</p>
                </td>
   </tr>
   <tr>
    <td valign="top" class="alignCenter">
                  <p>All the Best, </p>
                <p>The buybuy BABY Team</p></td>
   </tr>
 </table>
        </td>
    </tr>
    <!-- // End Template Main Content \\ -->', 
 EMAIL_FLAG='ON', 
 CONFIGURABLE_TYPE='',
 CHANNEL='',
 CHANNEL_THEME='' 
 WHERE TRANSLATION_ID='TR100005';
 
 UPDATE BBB_CORE_PRV.BBB_EMAIL_TRANSLATIONS SET SITE_ID='BedBathCanada', 
 LOCALE_ID='en_US', 
 EMAIL_FROM='customer.service@bedbathandbeyond.ca', 
 EMAIL_SUBJECT='Password Reset Request',
 EMAIL_BODY='<!-- // Begin Template Main Content \\ -->
    <tr>
        <td align="left" valign="top">
        	<table border="0" cellpadding="4" cellspacing="4" width="100%" style="font-size:14px; line-height:14px; color:#666666;font-family:Arial;">
            <tr>
				<td height="5" style="line-height:5px;">
                </td>
			</tr>
			<tr>
				<td valign="top"><h3 class="resetTextSize" style="padding:0;margin:0;font-family:arial;">Dear frmData_firstName,</h3>
                </td>
			</tr>
            <tr>
				<td valign="top"><p class="resetTextSize" style="margin:0;padding:0;color:#666666;font-family:Arial;font-size:14px;line-height:16px;">You requested a password reset.</p>
                </td>
			</tr>
			<tr>
			 <td valign="top"><a style="color: #273691;" href="account_login_url?token=encoded_token" target="_blank">Reset Password</a>
			 </tr>
            <tr>
				<td valign="top"><p class="resetTextSize" style="margin:0;padding:0;color:#666666;font-family:Arial;font-size:14px;line-height:18px;">If you did not request a password reset please contact customer service at  <a style="color: #273691;"href="mailto:customer.service@bedbath.ca">customer.service@bedbath.ca</a> or call 1-800-GO-BEYOND&reg; (1-800-462-3966) and we can assist you.</p>
                </td>
			</tr>
			<tr>
				<td valign="top" class="alignCenter">
                <p>Sincerely,<br>
  Customer Service<br>
  Bed Bath &amp; Beyond Inc.<br>
  customer.service@bedbath.ca</p></td>
			</tr>			
	</table>
        </td>
    </tr>
    <!-- // End Template Main Content \\ -->  ', 
 EMAIL_FLAG='ON',
 CONFIGURABLE_TYPE='',
 CHANNEL='',
 CHANNEL_THEME='' 
 WHERE TRANSLATION_ID='TR100006';
 

 ---- update links : BBB_CORE_PRV.BBB_LINK

 
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Corporate Sales" href="/store/static/CorporateSalesReport">Corporate Sales</a>', TEXT='Corporate Sales' WHERE LINK_ID='DC112700001';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/selfservice/FindStore" title="Find A Store">Find A Store</a>', TEXT='Find A Store' WHERE LINK_ID='DC112700002';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/TermsOfUse" title="Terms of Use">Terms of Use</a>', TEXT='Terms of Use' WHERE LINK_ID='DC112700003';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/Careers" title="Careers">Careers</a>', TEXT='Careers' WHERE LINK_ID='DC112700004';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Investor Relations" href="http://phx.corporate-ir.net/phoenix.zhtml?c=97860&p=irol-irhome" >Investor Relations</a>', TEXT='Investor Relations' WHERE LINK_ID='DC112700005';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Media Relations" href="/store/static/MediaRelations">Media Relations</a>', TEXT='Media Relations' WHERE LINK_ID='DC112700006';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/CorporateResponsibilityReport" title="Corporate Responsibility">Corporate Responsibility</a>', TEXT='Corporate Responsibility' WHERE LINK_ID='DC112700007';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/page/brands" title="Shop by Brand">Shop by Brand</a>', TEXT='Shop by Brand' WHERE LINK_ID='DC112700008';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/GiftCardHomePage" title="Shop Gift Cards">Shop Gift Cards</a>', TEXT='Shop Gift Cards' WHERE LINK_ID='DC112700010';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/registry/PersonalizedInvitations" title="Personalized Invitations">Shop Personalized Invitations</a>', TEXT='Shop Personalized Invitations' WHERE LINK_ID='DC112700011';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Glossary" href="/store/static/GlossaryPage" >Glossary</a>', TEXT='Glossary' WHERE LINK_ID='DC112700012';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="http://video.bedbathandbeyond.com/" target="_blank" title="Videos">Videos</a>', TEXT='Videos' WHERE LINK_ID='DC112700013';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Product Guides" href="/store/static/GuidesPage" >Guides</a>', TEXT='Guides' WHERE LINK_ID='DC112700014';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/SafetyAndRecalls" title="Safety & Recalls">Safety & Recalls</a>', TEXT='Safety & Recalls' WHERE LINK_ID='DC112700015';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="http://bedbathandbeyond.custhelp.com/" target="_blank" title="FAQ">FAQs</a>', TEXT='FAQs' WHERE LINK_ID='DC112700017';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a class="popup" href="/store/static/GiftPackagingPopUp" title="Gift Packaging">Gift Packaging</a>', TEXT='Gift Packaging' WHERE LINK_ID='DC112700018';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a class="popup" href="/store/static/BopusInfo" target="_blank" >Order Online - Pick up in store</a>', TEXT='Order Online - Pick up in store' WHERE LINK_ID='DC112700019';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/InternationalShippingPolicies" title="International Shipping Info">International Shipping</a>', TEXT='International Shipping' WHERE LINK_ID='DC112700020';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/ShippingPolicies" title="Shipping Info">Shipping Info</a>', TEXT='Shipping Info' WHERE LINK_ID='DC112700021';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/pricematch" title="Price Match Guarantee">Price Match Guarantee</a>', TEXT='Price Match Guarantee' WHERE LINK_ID='DC112700022';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/EasyReturns" title="Easy Returns">Easy Returns</a>', TEXT='Easy Returns' WHERE LINK_ID='DC112700023';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/selfservice/ContactUs" title="Contact Us">Contact Us / Feedback</a>', TEXT='Contact Us / Feedback' WHERE LINK_ID='DC112700024';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/selfservice/FindStore" title="Find A Store">Find A Store</a>', TEXT='Find A Store' WHERE LINK_ID='DC112700025';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/TermsOfUse" title="Terms of Use">Terms of Use</a>', TEXT='Terms of Use' WHERE LINK_ID='DC112700026';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/Careers" title="Careers">Careers</a>', TEXT='Careers' WHERE LINK_ID='DC112700027';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Media Relations" href="/store/static/MediaRelations">Media Relations</a>', TEXT='Media Relations' WHERE LINK_ID='DC112700028';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="About Us" href="/store/static/BabyAboutUs">About Us</a>', TEXT='About Us' WHERE LINK_ID='DC112700029';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Shop by Brand" href="/store/page/brands" >Shop by Brand</a>', TEXT='Shop by Brand' WHERE LINK_ID='DC112700030';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Shop Gift Cards" href="/store/static/GiftCardHomePage" >Shop Gift Cards</a>', TEXT='Shop Gift Cards' WHERE LINK_ID='DC112700032';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/registry/PersonalizedInvitations" title="Personalized Invitations">Shop Personalized Invitations</a>', TEXT='Shop Personalized Invitations' WHERE LINK_ID='DC112700033';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/registry/adoptionchecklist2" title="Adoption Checklist: 12+">Adoption Checklist: 12+</a>', TEXT='Adoption Checklist: 12+' WHERE LINK_ID='DC112700034';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/registry/adoptionchecklist1" title="Adoption Checklist: 0 - 12 Months">Adoption Checklist: 0 - 12 Months</a>', TEXT='Adoption Checklist: 0 - 12 Months' WHERE LINK_ID='DC112700035';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/registry/RegistryChecklist" title="Registry Checklist">Registry Checklist</a>', TEXT='Registry Checklist' WHERE LINK_ID='DC112700036';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Glossary" href="/store/static/GlossaryPage" >Glossary</a>', TEXT='Glossary' WHERE LINK_ID='DC112700037';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="http://video.buybuybaby.com/" target="_blank">Videos</a>', TEXT='Videos' WHERE LINK_ID='DC112700038';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/registry/GuidesAndAdviceLandingPage" title="Guides & Advice">Guides & Advice</a>', TEXT='Guides & Advice' WHERE LINK_ID='DC112700039';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Shop Our Catalogs" href="http://www.buybuybaby.com/store/static/Catalogs">Shop Our Catalogs</a>', TEXT='Shop Our Catalogs' WHERE LINK_ID='DC112700040';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/SafetyAndRecalls" title="Safety & Recalls">Safety & Recalls</a>', TEXT='Safety & Recalls' WHERE LINK_ID='DC112700041';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a target="_blank" onclick="window.open(''https://eqa.www.itestbaby.com/store/account/check_balance.jsp'',''gc'', ''scrollbars=no,top=500,left=500,width=415,height=175,menubar=no'');return false" href="">Check Gift Card Balance</a>', TEXT='Check Gift Card Balance' WHERE LINK_ID='DC112700042';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/page/BabyRegistry" title="Find a Registry">Find a Registry</a>', TEXT='Find a Registry' WHERE LINK_ID='DC112700043';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/page/BabyRegistry" title="Create a Registry">Create a Registry</a>', TEXT='Create a Registry' WHERE LINK_ID='DC112700044';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="http://buybuybabycom.custhelp.com/app/answers/list" target=" _blank" title="FAQ">FAQs</a>', TEXT='FAQs' WHERE LINK_ID='DC112700045';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/registry/bbBPicturePeople" title="Picture People">Picture People</a>', TEXT='Picture People' WHERE LINK_ID='DC112700046';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a class="popup" href="/store/static/GiftPackagingPopUp" title="Gift Packaging">Gift Packaging</a>', TEXT='Gift Packaging' WHERE LINK_ID='DC112700047';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a class="popup" target="_blank" href="/store/static/BopusInfo">Reserve Online Pay in Store</a>', TEXT='Reserve Online Pay in Store' WHERE LINK_ID='DC112700048';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/InternationalShippingPolicies" title="International Shipping Info">International Shipping</a>', TEXT='International Shipping' WHERE LINK_ID='DC112700049';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/ShippingPolicies" title="Shipping Info">Shipping Info</a>', TEXT='Shipping Info' WHERE LINK_ID='DC112700050';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Price Match Guarantee" href="/store/static/pricematch">Price Match Guarantee</a>', TEXT='Price Match Guarantee' WHERE LINK_ID='DC112700051';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/EasyReturns" title="Easy Returns">Easy Returns</a>', TEXT='Easy Returns' WHERE LINK_ID='DC112700052';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/selfservice/ContactUs" title="Contact Us">Contact Us / Feedback</a>', TEXT='Contact Us / Feedback' WHERE LINK_ID='DC112700053';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Corporate Sales" href="/store/static/CorporateSalesReport">Corporate Sales</a>', TEXT='Corporate Sales' WHERE LINK_ID='DC112700054';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/selfservice/CanadaStoreLocator" title="Find A Store">Find A Store</a>', TEXT='Find A Store' WHERE LINK_ID='DC112700055';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/accessibilityOntario" >Accessibility</a>', TEXT='Accessibility' WHERE LINK_ID='DC112700056';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/TermsOfUse" title="Terms of Use">Terms of Use</a>', TEXT='Terms of Use' WHERE LINK_ID='DC112700057';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/Careers" title="Careers">Careers</a>', TEXT='Careers' WHERE LINK_ID='DC112700058';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Investor Relations" href="http://phx.corporate-ir.net/phoenix.zhtml?c=97860&p=irol-irhome" >Investor Relations</a>', TEXT='Investor Relations' WHERE LINK_ID='DC112700059';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Media Relations" href="/store/static/MediaRelations">Media Relations</a>', TEXT='Media Relations' WHERE LINK_ID='DC112700060';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Shop by Brand" href="/store/page/brands" >Shop by Brand</a>', TEXT='Shop by Brand' WHERE LINK_ID='DC112700061';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Clearance" href="/store/category/Clearance/20009/">Shop all Clearance</a>', TEXT='Shop all Clearance' WHERE LINK_ID='DC112700062';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Shop Gift Cards" href="/store/static/GiftCardHomePage" >Shop Gift Cards</a>', TEXT='Shop Gift Cards' WHERE LINK_ID='DC112700063';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Glossary" href="/store/static/GlossaryPage" >Glossary</a>', TEXT='Glossary' WHERE LINK_ID='DC112700064';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Videos" target="_blank" href="http://video.bedbathandbeyond.ca/" >Videos</a>', TEXT='Videos' WHERE LINK_ID='DC112700065';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Product Guides" href="/store/static/GuidesPage" >Guides</a>', TEXT='Guides' WHERE LINK_ID='DC112700066';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/SafetyAndRecalls" title="Safety & Recalls">Safety & Recalls</a>', TEXT='Safety & Recalls' WHERE LINK_ID='DC112700067';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a target="_blank" onclick="window.open(''https://eqa.www.itestbbby.ca/store/account/check_balance.jsp'',''gc'', ''scrollbars=no,top=500,left=500,width=415,height=175,menubar=no'');return false" href="">Check Gift Card Balance</a>', TEXT='Check Gift Card Balance' WHERE LINK_ID='DC112700068';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="http://bedbathandbeyondca.custhelp.com/" target="_blank" title="FAQ">FAQs</a>', TEXT='FAQs' WHERE LINK_ID='DC112700069';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a class="popup" href="/store/static/GiftPackagingPopUp" title="Gift Packaging">Gift Packaging</a>', TEXT='Gift Packaging' WHERE LINK_ID='DC112700070';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a class="popup" href="/store/static/BopusInfo" target="_blank" >Order Online - Pick up in store</a>', TEXT='Order Online - Pick up in store' WHERE LINK_ID='DC112700071';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/ShippingPolicies" title="Shipping Info">Shipping Info</a>', TEXT='Shipping Info' WHERE LINK_ID='DC112700072';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/pricematch" title="Price Match Guarantee">Price Match Guarantee</a>', TEXT='Price Match Guarantee' WHERE LINK_ID='DC112700073';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/static/EasyReturns" title="Easy Returns">Easy Returns</a>', TEXT='Easy Returns' WHERE LINK_ID='DC112700074';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a href="/store/selfservice/ContactUs" title="Contact Us">Contact Us / Feedback</a>', TEXT='Contact Us / Feedback' WHERE LINK_ID='DC112700075';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a target="_blank" onclick="window.open(''https://eqa.www.itestbbby.com/store/account/check_balance.jsp'',''gc'', ''scrollbars=no,top=500,left=500,width=415,height=175,menubar=no'');return false" href="">Check Gift Card Balance</a>', TEXT='Check Gift Card Balance' WHERE LINK_ID='DC112700016';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Clearance" href="/store/category/clearance-savings/30010/">Shop all Clearance</a>', TEXT='Shop all Clearance' WHERE LINK_ID='DC112700031';
UPDATE BBB_CORE_PRV.BBB_LINK SET LINK='<a title="Clearance" href="/store/category/clearance-savings/10009/">Shop all Clearance</a>', TEXT='Shop all Clearance' WHERE LINK_ID='DC112700009';

--- insert link : BBB_CORE_PRV.BBB_LINK

INSERT INTO BBB_CORE_PRV.BBB_LINK (LINK_ID,LINK,TEXT) values ('DC118400001','couponWalletRegistration','MY OFFERS');

--- insert KSpromobox data : BBB_CORE_PRV.BBB_KS_PROMO_CONTAINER

Insert into BBB_CORE_PRV.BBB_KS_PROMO_CONTAINER (PROMO_CONTAINER_ID,CONTAINER_PLACE_HOLDER,CONTAINER_TITLE,CONTAINER_PAGE_LOCATION,CUSTOMER_TYPE) values ('DC1500004','6','RegistryCheckListWedding','0','0');
Insert into BBB_CORE_PRV.BBB_KS_PROMO_REG_TYPE (PROMO_CONTAINER_ID,REG_TYPE_ID) values ('DC1500004','300001'); 
Insert into BBB_CORE_PRV.BBB_KS_PROMO_BOXES (PROMO_CONTAINER_ID,PROMO_BOX_ID,SEQUENCE_NUM) values ('DC1500004','DC1bbPBX1260004','0'); 
Insert into BBB_CORE_PRV.BBB_KS_PROMOBOX_SITE (PROMO_CONTAINER_ID,SITE_ID) values ('DC1500004','BedBathCanada'); 
Insert into BBB_CORE_PRV.BBB_KS_PROMOBOX_SITE (PROMO_CONTAINER_ID,SITE_ID) values ('DC1500004','BedBathUS');
Insert into BBB_CORE_PRV.BBB_KS_PROMOBOX_SITE (PROMO_CONTAINER_ID,SITE_ID) values ('DC1500004','BuyBuyBaby');
Insert into BBB_CORE_PRV.BBB_KS_PROMO_CONTAINR_CHANNELS (PROMO_CONTAINER_ID,CHANNELS_ID) values ('DC1500004','DesktopWeb');

--- insert mobile navigation template : BBB_CORE_PRV.BBB_LINKS

INSERT INTO BBB_CORE_PRV.BBB_LINKS (NAV_LINK_ID,SEQUENCE_NUM,LINK_ID) values ('DC1700002 (copy)','18','DC118400001');
 
--- update promo image : BBB_CORE_PRV.BBB_PROMOIMAGE

UPDATE BBB_CORE_PRV.BBB_PROMOIMAGE SET IMAGE_URL = '//s7d9.scene7.com/is/image/BedBathandBeyond/images/registry/icon_20121220_bridalannouncements.jpg', IMAGE_ALT_TEXT='Announcement Cards', LINK_LABEL='Announcement Cards', LINK_URL='/store/printCards/printCardsLanding.jsp' WHERE PROMO_IMAGE_ID = 'LTPI30042';
UPDATE BBB_CORE_PRV.BBB_PROMOIMAGE SET IMAGE_URL = '//s7d9.scene7.com/is/image/BedBathandBeyond/images/registry/icon_20121220_bridalchecklist.jpg', IMAGE_ALT_TEXT='Registry Checklist', LINK_LABEL='Registry Checklist', LINK_URL='/store/registry/RegistryChecklist' WHERE PROMO_IMAGE_ID = 'LTPI30038';
UPDATE BBB_CORE_PRV.BBB_PROMOIMAGE SET IMAGE_URL = '//s7d9.scene7.com/is/image/BedBathandBeyond/images/registry/icon_20121220_bridalguides.jpg', IMAGE_ALT_TEXT='Guides & Advice', LINK_LABEL='Guides & Advice', LINK_URL='/store/registry/GuidesAndAdviceLandingPage' WHERE PROMO_IMAGE_ID = 'LTPI30039';
UPDATE BBB_CORE_PRV.BBB_PROMOIMAGE SET IMAGE_URL = '//s7d9.scene7.com/is/image/BedBathandBeyond/images/registry/icon_20121220_bridalbook.jpg', IMAGE_ALT_TEXT='Wedding Registry Book', LINK_LABEL='Wedding Registry Book', LINK_URL='/store/bbregistry/BridalBook' WHERE PROMO_IMAGE_ID = 'DC1500001';
UPDATE BBB_CORE_PRV.BBB_PROMOIMAGE SET IMAGE_URL = '//s7d9.scene7.com/is/image/BedBathandBeyond/personalizedinvitations?$other$', IMAGE_ALT_TEXT='Personalized Invitations', LINK_LABEL='Personalized Invitations', LINK_URL='/store/registry/PersonalizedInvitations' WHERE PROMO_IMAGE_ID = 'PUBbbPI40002';
UPDATE BBB_CORE_PRV.BBB_PROMOIMAGE SET IMAGE_URL = '//s7d9.scene7.com/is/image/BedBathandBeyond/images/registry/icon_20121220_bridalchecklist.jpg', IMAGE_ALT_TEXT='Registry Checklist', LINK_LABEL='Registry Checklist', LINK_URL='/store/registry/RegistryChecklist' WHERE PROMO_IMAGE_ID = 'DC1800001';

--- update mobile site static content : BBB_CORE_PRV.BBB_WS_ST_CONTENT

DECLARE
  str varchar2(32767);
BEGIN
  str := '<style>
.alert, .alert h4 {
	color: #1a1a1a;
}
.alert {
	border: 0;
	border-left: 5px solid #fad611;
}
.alert {
	border-radius: 0px;
}
h3 {
	margin-top: 10px;
}
.bulletsReset li p {
	margin-top: 5px;
}
</style>
<p>We want you to love what you buy whether you buy it in any of our stores or online! If you are not fully satisfied with your purchase, we will help you find the item that’s right for you.</p>
</div>
<div class="row-fluid">
  <div class="accordion accordionProduct patternGrey" id="shippingAccordion">
    <div class="accordion-group accordion-caret">
      <div class="accordion-heading"><a class="accordion-toggle" data-toggle="collapse" data-parent="#shippingAccordion" href="#collapseShippingOne1"><span class="collapsedIcon icon pull-right"></span><span class="shippingMethodTitle"> General Returns </span></a></div>
      <div id="collapseShippingOne1" class="accordion-body collapse">
        <div class="accordion-inner consistentPadding text-left">
          <div class="consistentPadding">
            <div class="bulletsReset">
              <ul>
                <li>
                  <h3 class="subHeading marTop_20 returnsReceipt">Returns with a Receipt</h3>
                  <p><strong>Returns with an Original Receipt</strong><br />
                    Returns can be made at any of our stores* nationwide or to our Returns Processing Center. Simply provide your original receipt or packing slip and we will complete an exchange or merchandise refund for the amount paid to the original form of payment**.</p>
                  <p>*Merchandise received from a Truck Delivery cannot be returned to our stores. Please see Truck Delivery Returns.</p>
                  <p>**Please see PayPal Returns for any purchase made using PayPal.</p>
                  <p>**Please note that whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</p>
                  <p><strong>Returns with a Gift Receipt</strong><br />
                    Merchandise returned using a Gift Receipt will either be exchanged or refunded in the form of a merchandise credit for the amount
                    paid. Please note that whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. 
                    This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</p>
                </li>
                <li>
                  <h3 class="subHeading returnsNoReceipt">Returns without a Receipt</h3>
                  <p>Don’t have a Receipt?
                    No problem. If the purchase was made in the last 365 days, we can attempt to locate the purchase using any of the following:
                  <div class="bulletsReset">
                    <ul>
                      <li>Store or Merchandise Credit Number</li>
                      <li>Gift Card Number</li>
                      <li>Order Number (if applicable)</li>
                      <li>Registry Number (if applicable)</li>
                    </ul>
                  </div>
                  </p>
                  <p>If we can''t find a record of the purchase, we will gladly either:</p>
                  <div class="bulletsReset">
                    <ul>
                      <li>Complete an exchange for the current selling price.</li>
                      <li>Provide a merchandise credit for the current selling price.</li>
                      <li>Complete a corporate refund request to research the original purchase.</li>
                      <p>Once the request has been completed:</p>
                      <div class="bulletsReset">
                        <ul>
                          <li>If the original purchase is located, we will complete an exchange or merchandise refund for the amount paid to the original form of payment.</li>
                          <li>If the original purchase is not located and the refund request is approved, we will complete a refund to a merchandise credit for the current selling pricef.</li>
                        </ul>
                      </div>
                    </ul>
                  </div>
                </li>
                <li>
                  <h3 class="subHeading returnsProcessing">Returns to Our Processing Center</h3>
          <p>Return postage labels are included with every purchase from our online store in the event you need to return an item. If you cannot locate the return postage label that was included with your order, please <a href="http://www.bedbathandbeyond.ca/store/selfservice/EasyReturns">click here</a> to print your free return shipping label. For returns from Military Post Office, APO, FPO and US Territories, please contact us at 1-800-GO-BEYOND (1-800-462-3966) or <a href="mailto:customer.service@bedbath.com">customer.service@bedbath.com</a> for further instructions.</p>
          <p>Please follow the steps below to complete your return:</p>
                  <div class="bulletsReset">
                    <ol>
                      <li>Please circle the item you wish to return on the bottom copy of your original packing invoice form.</li>
                      <li>Choose a reason for the return from the back of the invoice and enter its number next to your circled item.</li>
                      <li>Provide a telephone number in case we have questions.</li>
                      <li>Detach the top copy of the invoice form and keep it for your records. Enclose the bottom copy with the items you are returning.</li>
                      <li>Pack your return securely and attach the pre-paid return label found in your original carton.</li>
                      <li>To return your package you can drop off at any Canada Post retail outlet or visit <a href="http://www.bedbathandbeyond.ca/returns">www.bedbathandbeyond.ca/returns</a> to print your free return shipping label. Please print a return label for each box sent.</li>
                      <li>Should you have any questions or comments, please feel free to call us at 1-800-GO-BEYOND, and a customer service representative will be pleased to assist you.</li>
                    </ol>
                  </div>
                </li>
                <li>
                  <h3 class="subHeading">Note for All Returns</h3>
                  <div class="bulletsReset">
                    <ol>
                      <li>Shipping, delivery and assembly charges are non-refundable.</li>
                      <li>All gift cards are non-refundable</li>
                      <li>Whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</li>
                      <li>Breast pumps may only be returned unopened and require proof of purchase.</li>
                      <li>Baby clothing and Destination Maternity items may only be returned with all tags intact and require proof of purchase. Returns of these items made more than 90 days after the purchase date will receive a merchandise credit for the current selling price.</li>
                      <li>Please see PayPal Returns for any purchase made using PayPal.</li>
                      <li>Please see Truck Delivery Returns for additional return restrictions.</li>
                    </ol>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="accordion-group accordion-caret">
      <div class="accordion-heading"><a class="accordion-toggle" data-toggle="collapse" data-parent="#shippingAccordion" href="#collapseShippingTwo2"><span class="shippingMethodTitle"> Paypal Returns</span><span class="collapsedIcon icon pull-right"></span></a></div>
      <div id="collapseShippingTwo2" class="accordion-body collapse">
        <div class="accordion-inner consistentPadding">
          <div class="consistentPadding">
            <div class="bulletsReset">
              <ul>
                <li>
                  <h3 class="subHeading marTop_20 paypalStores">To Our Stores or To Our Returns Center</h3>
                  <p>Merchandise paid for with PayPal will be refunded in the form of a Merchandise Credit.</p>
                </li>
                <li>
                  <h3 class="subHeading">Note for All Returns</h3>
                  <div class="bulletsReset">
                    <ol>
                      <li>Shipping, delivery and assembly charges are non-refundable.</li>
                      <li>All gift cards are non-refundable</li>
                      <li>Whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</li>
                      <li>Breast pumps may only be returned unopened and require proof of purchase.</li>
                      <li>Baby clothing and Destination Maternity items may only be returned with all tags intact and require proof of purchase. Returns of these items made more than 90 days after the purchase date will receive a merchandise credit for the current selling price.</li>
                      <li>Please see PayPal Returns for any purchase made using PayPal.</li>
                      <li>Please see Truck Delivery Returns for additional return restrictions.</li>
                    </ol>
                  </div>
                </li>
                <a name="truck"></a>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="accordion-group accordion-caret">
      <div class="accordion-heading"><a class="accordion-toggle" data-toggle="collapse" data-parent="#shippingAccordion" href="#collapseShippingThree"><span class="shippingMethodTitle"> Truck Delivery Returns</span><span class="collapsedIcon icon pull-right"></span></a></div>
      <div id="collapseShippingThree" class="accordion-body collapse">
        <div class="accordion-inner consistentPadding">
          <div class="consistentPadding">
            <p>We want you to enjoy your purchase for years to come. That’s why it’s important to understand what to do in the event that you have an issue with your purchase.</p>
            <div class="bulletsReset marLeft_10">
              <ul>
                <li>
                  <h3 class="subHeading truckAccept">Accepting Your Truck Delivery</h3>
                  <p>Someone over the age of 18 must be present to sign for your delivery. Also, please note that once your delivery is scheduled and confirmed, if you are not home to accept the delivery, you will be responsible for all shipping, storage and vendor charges (such as cancellation or restocking fees) as specified in the chart below.</p>
                  <p class="bold">While the delivery person is present AND prior to signing for delivery:</p>
                  <div class="bulletsReset">
                    <ul>
                      <li>Open and inspect the item(s).</li>
                      <li>For any damage (even minimal), write “Package Damaged” on the delivery form.</li>
                      <li>Call 844-4BBBHOME (844-422-2466) immediately to report any defects or damages.</li>
                    </ul>
                  </div>
                  <p class="bold">After Accepting Your Delivery</p>
                  <div class="bulletsReset">
                    <ol>
                      <li>Within 48 Hours<br>
                        If you notice missing or damaged parts, call 844-4BBBHOME (844-422-2466) within 48 hours and we will send you replacement parts free of charge.</li>
                      <li>Within 30 Days<br>
                        Item(s) may be returned for any reason as long as the following conditions are met:
                        <ul>
                          <li>The item(s) must be in its original condition.</li>
                          <li>Truck Delivery purchases may NOT be returned to a Bed Bath & Beyond store. Our stores are not equipped to handle these types of returns.</li>
                          <li>Customer is responsible for all shipping, storage and vendor charges (such as cancellation or restocking fees) as specified in the Chart below.</li>
                        </ul>
                        <p>To schedule a return within 30 days under these conditions, call our Delivery Customer Service team at 844-4BBBHOME (844-422-2466) between the hours of 8:00 a.m. to 11:00 p.m. Monday – Saturday (Eastern Time). We will make arrangements to have our carrier contact you to schedule a pick up of your return.</p>
                      </li>
                      <li>After 30 Days<br>
                        Returns will not be accepted after 30 days. For issues after 30 days, please contact the manufacturer.</li>
                    </ol>
                  </div>
                </li>
                <li>
                  <h3 class="subHeading truckCancel">Cancellation &amp; Return Schedule Fee Chart</h3>
                  <table width="976" cellpadding="5" id="LTLTable">
                    <tr class="noBorder">
                      <td class="bold FS15">Cancellation &amp; Return Schedule</td>
                      <td class="bold FS15">Fee</td>
                    </tr>
                    <tr>
                      <td>Orders Cancelled within 24 hours of purchase</td>
                      <td>No Fee</td>
                    </tr>
                    <tr>
                      <td>Orders Cancelled after 24 hours of purchase and before Shipper contact</td>
                      <td>Restocking fee (up to 20% of purchase price)</td>
                    </tr>
                    <tr>
                      <td>Orders Cancelled after Shipper Contact, at Delivery, or after Delivery</td>
                      <td>Restocking fee (up to 20% of purchase price) + Cost of Return Freight</td>
                    </tr>
                  </table>
                  <p>The restocking fee covers administrative, shipping, storage, and all associated fees incurred during the processing and shipping of your order.</p>
                </li>
                <li>
        <h3 class="subHeading mattressReturnsHeading">Adult Size Mattress Returns: What You Should Know</h3>
        <p>The transition from your old mattress to a new mattress can feel very different. In many cases, it can take about 30 days for your body to adjust to your new mattress. If after 30 days you are not completely satisfied, we will allow a one-time exchange up to 120 days from delivery.</p>
        <p class="bold">Please Note:<br>
          Mattresses can only be exchanged:</p>
        <div class="bulletsReset">
          <ul class="mattress">
            <li>Within 120 days of delivery,</li>
            <li>Are not eligible to be returned</li>
            <li>By calling 1-844-4BBBHOME (844-422-2466) and cannot be exchanged at any buybuy BABY store. Our stores do not sell mattresses and they are not equipped to handle these types of exchanges.</li>
          </ul>
        </div>
        <p class="bold">If you decide to exchange your mattress, only the purchase price of the mattress will be applied to your exchange.<br>
          Customers are responsible for:</p>
        <div class="bulletsReset">
          <ul class="mattress noMarBot">
            <li>Original shipping charges</li>
            <li>Return shipping charges</li>
            <li>Vendor surcharges (such as cancellation or restocking fees), and</li>
            <li>Shipping charges for the new mattress</li>
          </ul>
        </div>
        <p class="noMarTop">To schedule an exchange, call our Home Delivery team at 1-844-4BBBHOME (844-422-2466) between the hours of 8:00 a.m. and 11:00 p.m. Monday – Saturday (Eastern Time).</p>
        </li>
                <li>
                  <h3 class="subHeading">Note for All Returns</h3>
                  <div class="bulletsReset">
                    <ol>
                      <li>Shipping, delivery and assembly charges are non-refundable.</li>
                      <li>All gift cards are non-refundable</li>
                      <li>Whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</li>
                      <li>Breast pumps may only be returned unopened and require proof of purchase.</li>
                      <li>Baby clothing and Destination Maternity items may only be returned with all tags intact and require proof of purchase. Returns of these items made more than 90 days after the purchase date will receive a merchandise credit for the current selling price.</li>
                      <li>Please see PayPal Returns for any purchase made using PayPal.</li>
                      <li>Please see Truck Delivery Returns for additional return restrictions.</li>
                    </ol>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<div class="consistentPadding">
';
  UPDATE BBB_CORE_PRV.BBB_WS_ST_CONTENT SET PAGE_NAME = '200', 
 BBB_PAGE_NAME='easyReturns',
 PAGE_COPY=str,
 PAGE_TITLE='Easy Returns'  
 WHERE STATIC_TEMPLATE_ID = 'DC1bbMSSCT50007';
END;
/
DECLARE
  str varchar2(32767);
BEGIN
  str := '<style>
.alert, .alert h4 {
 color: #1a1a1a;
}
.alert {
 border: 0;
 border-left: 5px solid #fad611;
}
.alert {
 border-radius: 0px;
}
h3 {
 margin-top: 10px;
}
.bulletsReset li p {
 margin-top: 5px;
}
</style>
<p class="marBottom_20">We want you to love what you buy whether you buy it in any of our stores or online! If you are not fully satisfied with your purchase, we will help you find the item that’s right for you.</p>
<div class="alert alert-alert">
  <h3>Update to our Un-Receipted Return Policy effective April 20, 2015</h3>
  <p>Effective April 20, 2015, if you do not have a receipt for a purchase made in the last 365 days, we can look up the purchase using either your credit card or gift registry used to make the purchase.</p>
  <p>If we can''t find a record of the purchase, we will gladly complete an exchange or provide you with a merchandise credit for the current selling price less 20%. This accounts for additional discounts or coupons that may have been applied.</p>
</div>
</div>
<div class="row-fluid">
<div class="accordion accordionProduct patternGrey" id="shippingAccordion">
  <div class="accordion-group accordion-caret">
    <div class="accordion-heading"><a class="accordion-toggle" data-toggle="collapse" data-parent="#shippingAccordion" href="#collapseShippingOne1"><span class="collapsedIcon icon pull-right"></span><span class="shippingMethodTitle"> General Returns </span></a></div>
    <div id="collapseShippingOne1" class="accordion-body collapse">
      <div class="accordion-inner consistentPadding text-left">
        <div class="consistentPadding">
          <div class="bulletsReset">
            <ul>
              <li>
                <h3 class="subHeading marTop_20 returnsReceipt">Returns with a Receipt</h3>
                <p><strong>Returns with an Original Receipt</strong><br />
                  Returns can be made at any of our stores* nationwide or to our Returns Processing Center. Simply provide your original receipt or packing slip and we will complete an exchange or merchandise refund for the amount paid to the original form of payment**.</p>
                <p>*Merchandise received from a Truck Delivery cannot be returned to our stores. Please see Truck Delivery Returns.</p>
                <p>**Please see PayPal Returns for any purchase made using PayPal.</p>
                <p>**Please note that whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</p>
                <p><strong>Returns with a Gift Receipt</strong><br />
                  Merchandise returned using a Gift Receipt will either be exchanged or refunded in the form of a merchandise credit for the amount
                  paid. Please note that whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. 
                  This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</p>
              </li>
              <li>
                <h3 class="subHeading returnsNoReceipt">Returns without a Receipt</h3>
                <p>Don’t have a Receipt?
                  No problem. If the purchase was made in the last 365 days, we can attempt to locate the purchase using any of the following:
                <div class="bulletsReset">
                  <ul>
                    <li>Credit/Debit Card</li>
                    <li>Store or Merchandise Credit Number</li>
                    <li>Gift Card Number</li>
                    <li>Order Number (if applicable)</li>
                    <li>Registry Number (if applicable)</li>
                    <li>Checking Account Number</li>
                  </ul>
                </div>
                </p>
                <p>If we can''t find a record of the purchase, we will gladly either:</p>
                <div class="bulletsReset">
                  <ul>
                    <li>Complete an exchange for the current selling price less 20%.</li>
                    <li>Provide a merchandise credit for the current selling price less 20%.</li>
                    <li>Complete a corporate refund request to research the original purchase.</li>
                    <p>Once the request has been completed:</p>
                    <div class="bulletsReset">
                      <ul>
                        <li>If the original purchase is located, we will complete an exchange or merchandise refund for the amount paid to the original form of payment.</li>
                        <li>If the original purchase is not located and the refund request is approved, we will complete a refund to a merchandise credit for the current selling price less 20%.</li>
                      </ul>
                    </div>
                  </ul>
                </div>
              </li>
              <li>
                <h3 class="subHeading returnsProcessing">Returns to Our Processing Center</h3>
          <p>Return postage labels are included with every purchase from our online store in the event you need to return an item. If you cannot locate the return postage label that was included with your order, please <a href="http://www.bedbathandbeyond.com/store/selfservice/EasyReturns">click here</a> to print your free return shipping label. For returns from Military Post Office, APO, FPO and US Territories, please contact us at 1-800-GO-BEYOND (1-800-462-3966) or <a href="mailto:customer.service@bedbath.com">customer.service@bedbath.com</a> for further instructions.</p>
          <p>Please follow the steps below to complete your return:</p>
                <div class="bulletsReset">
                  <ol>
                    <li>Please circle the item you wish to return on the bottom copy of your original packing invoice form.</li>
                    <li>Choose a reason for the return from the back of the invoice and enter its number next to your circled item.</li>
                    <li>Provide a telephone number in case we have questions.</li>
                    <li>Detach the top copy of the invoice form and keep it for your records. Enclose the bottom copy with the items you are returning.</li>
                    <li>Pack your return securely and attach the pre-paid return label found in your original carton.</li>
                    <li>To return your package you may use your regular FedEx scheduled pick up or drop off at a FedEx location (visit <a href="http://www.fedex.com/locate">fedex.com/locate</a> or call 1-800-GoFedEx (1-800-463-3339), and say “find a location”). You can schedule a Ground Pick by going to <a href="http://www.fedex.com/returnpickup">fedex.com/returnpickup</a> or call 1-800-GoFedEx and say “Return Manager”.</li>
                    <li>Should you have any questions or comments, please feel free to call us at 1-800-GO-BEYOND, and a customer service representative will be pleased to assist you.</li>
                  </ol>
                </div>
                <p>You may also mail returns via the US Postal Service to the address below. We recommend that you insure the package prior to shipment. Enclosing your original packing invoice will help us expedite your return.</p>
                <ul>
                  <li class="address">Bed Bath &amp; Beyond</li>
                  <li class="address">Returns Processing / Door 39</li>
                  <li class="address">1001 West Middlesex Avenue</li>
                  <li class="address">Port Reading, New Jersey 07064 </li>
                </ul>
              </li>
              <li>
                <h3 class="subHeading">Note for All Returns</h3>
                <div class="bulletsReset">
                  <ol>
                    <li>Shipping, delivery and assembly charges are non-refundable.</li>
                    <li>All gift cards are non-refundable</li>
                    <li>Whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</li>
                    <li>Breast pumps may only be returned unopened and require proof of purchase.</li>
                    <li>Baby clothing and Destination Maternity items may only be returned with all tags intact and require proof of purchase. Returns of these items made more than 90 days after the purchase date will receive a merchandise credit for the current selling price.</li>
                    <li>Please see PayPal Returns for any purchase made using PayPal.</li>
                    <li>Please see Truck Delivery Returns for additional return restrictions.</li>
                  </ol>
                </div>
                <p>To return merchandise to a store, please <a href="/m/storeLocator">click here</a> to find the one closest to you. To return merchandise to our Returns Processing Center, <a href="https://www.google.com/maps/dir//1001+Middlesex+Ave,+Port+Reading,+NJ+07064/@40.5685813,-74.2424572,17z/data=!4m13!1m4!3m3!1s0x89c3b4e9ee19ad05:0x24e963aa2887d012!2s1001+Middlesex+Ave,+Port+Reading,+NJ+07064!3b1!4m7!1m0!1m5!1m1!1s0x89c3b4e9ee19ad05:0x24e963aa2887d012!2m2!1d-74.2424572!2d40.5685813" target="_blank">click here</a> for directions and details.</p>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="accordion-group accordion-caret">
    <div class="accordion-heading"><a class="accordion-toggle" data-toggle="collapse" data-parent="#shippingAccordion" href="#collapseShippingTwo2"><span class="shippingMethodTitle"> Paypal Returns</span><span class="collapsedIcon icon pull-right"></span></a></div>
    <div id="collapseShippingTwo2" class="accordion-body collapse">
      <div class="accordion-inner consistentPadding">
        <div class="consistentPadding">
          <div class="bulletsReset">
            <ul>
              <li>
                <h3 class="subHeading marTop_20 paypalStores">To Our Stores</h3>
                <p>Merchandise paid for with PayPal will be refunded in the form of a Merchandise Credit.</p>
              </li>
              <li>
                <h3 class="subHeading paypalCenter">To Our Returns Center</h3>
                <p>Merchandise paid for with PayPal will be refunded back to PayPal. Please see below regarding shipping fees.</p>
              </li>
              <li>
                <h3 class="subHeading">Note for All Returns</h3>
                <div class="bulletsReset">
                  <ol>
                    <li>Shipping, delivery and assembly charges are non-refundable.</li>
                    <li>All gift cards are non-refundable</li>
                    <li>Whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</li>
                    <li>Breast pumps may only be returned unopened and require proof of purchase.</li>
                    <li>Baby clothing and Destination Maternity items may only be returned with all tags intact and require proof of purchase. Returns of these items made more than 90 days after the purchase date will receive a merchandise credit for the current selling price.</li>
                    <li>Please see PayPal Returns for any purchase made using PayPal.</li>
                    <li>Please see Truck Delivery Returns for additional return restrictions.</li>
                  </ol>
                </div>
                <p>To return merchandise to a store, please <a href="/m/storeLocator">click here</a> to find the one closest to you. To return merchandise to our Returns Processing Center, <a href="https://www.google.com/maps/dir//1001+Middlesex+Ave,+Port+Reading,+NJ+07064/@40.5685813,-74.2424572,17z/data=!4m13!1m4!3m3!1s0x89c3b4e9ee19ad05:0x24e963aa2887d012!2s1001+Middlesex+Ave,+Port+Reading,+NJ+07064!3b1!4m7!1m0!1m5!1m1!1s0x89c3b4e9ee19ad05:0x24e963aa2887d012!2m2!1d-74.2424572!2d40.5685813" target="_blank">click here</a> for directions and details.</p>
              </li>
              <a name="truck"></a>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="accordion-group accordion-caret">
    <div class="accordion-heading"><a class="accordion-toggle" data-toggle="collapse" data-parent="#shippingAccordion" href="#collapseShippingThree"><span class="shippingMethodTitle"> Truck Delivery Returns</span><span class="collapsedIcon icon pull-right"></span></a></div>
    <div id="collapseShippingThree" class="accordion-body collapse">
      <div class="accordion-inner consistentPadding">
        <div class="consistentPadding">
          <p>We want you to enjoy your purchase for years to come. That’s why it’s important to understand what to do in the event that you have an issue with your purchase.</p>
          <div class="bulletsReset marLeft_10">
            <ul>
              <li>
                <h3 class="subHeading truckAccept">Accepting Your Truck Delivery</h3>
                <p>Someone over the age of 18 must be present to sign for your delivery. Also, please note that once your delivery is scheduled and confirmed, if you are not home to accept the delivery, you will be responsible for all shipping, storage and vendor charges (such as cancellation or restocking fees) as specified in the chart below.</p>
                <p class="bold">While the delivery person is present AND prior to signing for delivery:</p>
                <div class="bulletsReset">
                  <ul>
                    <li>Open and inspect the item(s).</li>
                    <li>For any damage (even minimal), write “Package Damaged” on the delivery form.</li>
                    <li>Call 844-4BBBHOME (844-422-2466) immediately to report any defects or damages.</li>
                  </ul>
                </div>
                <p class="bold">After Accepting Your Delivery</p>
                <div class="bulletsReset">
                  <ol>
                    <li>Within 48 Hours<br>
                      If you notice missing or damaged parts, call 844-4BBBHOME (844-422-2466) within 48 hours and we will send you replacement parts free of charge.</li>
                    <li>Within 30 Days<br>
                      Item(s) may be returned for any reason as long as the following conditions are met:
                      <ul>
                        <li>The item(s) must be in its original condition.</li>
                        <li>Truck Delivery purchases may NOT be returned to a Bed Bath & Beyond store. Our stores are not equipped to handle these types of returns.</li>
                        <li>Customer is responsible for all shipping, storage and vendor charges (such as cancellation or restocking fees) as specified in the Chart below.</li>
                      </ul>
                      <p>To schedule a return within 30 days under these conditions, call our Delivery Customer Service team at 844-4BBBHOME (844-422-2466) between the hours of 8:00 a.m. to 11:00 p.m. Monday – Saturday (Eastern Time). We will make arrangements to have our carrier contact you to schedule a pick up of your return.</p>
                    </li>
                    <li>After 30 Days<br>
                      Returns will not be accepted after 30 days. For issues after 30 days, please contact the manufacturer.</li>
                  </ol>
                </div>
              </li>
              <li>
                <h3 class="subHeading truckCancel">Cancellation &amp; Return Schedule Fee Chart</h3>
                <table width="976" cellpadding="5" id="LTLTable">
                  <tr class="noBorder">
                    <td class="bold FS15">Cancellation &amp; Return Schedule</td>
                    <td class="bold FS15">Fee</td>
                  </tr>
                  <tr>
                    <td>Orders Cancelled within 24 hours of purchase</td>
                    <td>No Fee</td>
                  </tr>
                  <tr>
                    <td>Orders Cancelled after 24 hours of purchase and before Shipper contact</td>
                    <td>Restocking fee (up to 20% of purchase price)</td>
                  </tr>
                  <tr>
                    <td>Orders Cancelled after Shipper Contact, at Delivery, or after Delivery</td>
                    <td>Restocking fee (up to 20% of purchase price) + Cost of Return Freight</td>
                  </tr>
                </table>
                <p>The restocking fee covers administrative, shipping, storage, and all associated fees incurred during the processing and shipping of your order.</p>
              </li>
              <li>
        <h3 class="subHeading mattressReturnsHeading">Adult Size Mattress Returns: What You Should Know</h3>
        <p>The transition from your old mattress to a new mattress can feel very different. In many cases, it can take about 30 days for your body to adjust to your new mattress. If after 30 days you are not completely satisfied, we will allow a one-time exchange up to 120 days from delivery.</p>
        <p class="bold">Please Note:<br>
          Mattresses can only be exchanged:</p>
        <div class="bulletsReset">
          <ul class="mattress">
            <li>Within 120 days of delivery,</li>
            <li>Are not eligible to be returned</li>
            <li>By calling 1-844-4BBBHOME (844-422-2466) and cannot be exchanged at any buybuy BABY store. Our stores do not sell mattresses and they are not equipped to handle these types of exchanges.</li>
          </ul>
        </div>
        <p class="bold">If you decide to exchange your mattress, only the purchase price of the mattress will be applied to your exchange.<br>
          Customers are responsible for:</p>
        <div class="bulletsReset">
          <ul class="mattress noMarBot">
            <li>Original shipping charges</li>
            <li>Return shipping charges</li>
            <li>Vendor surcharges (such as cancellation or restocking fees), and</li>
            <li>Shipping charges for the new mattress</li>
          </ul>
        </div>
        <p class="noMarTop">To schedule an exchange, call our Home Delivery team at 1-844-4BBBHOME (844-422-2466) between the hours of 8:00 a.m. and 11:00 p.m. Monday – Saturday (Eastern Time).</p>
        </li>
              <li>
                <h3 class="subHeading">Note for All Returns</h3>
                <div class="bulletsReset">
                  <ol>
                    <li>Shipping, delivery and assembly charges are non-refundable.</li>
                    <li>All gift cards are non-refundable</li>
                    <li>Whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</li>
                    <li>Breast pumps may only be returned unopened and require proof of purchase.</li>
                    <li>Baby clothing and Destination Maternity items may only be returned with all tags intact and require proof of purchase. Returns of these items made more than 90 days after the purchase date will receive a merchandise credit for the current selling price.</li>
                    <li>Please see PayPal Returns for any purchase made using PayPal.</li>
                    <li>Please see Truck Delivery Returns for additional return restrictions.</li>
                  </ol>
                </div>
                <p>To return merchandise to a store, please <a href="/m/storeLocator">click here</a> to find the one closest to you. To return merchandise to our Returns Processing Center, <a href="https://www.google.com/maps/dir//1001+Middlesex+Ave,+Port+Reading,+NJ+07064/@40.5685813,-74.2424572,17z/data=!4m13!1m4!3m3!1s0x89c3b4e9ee19ad05:0x24e963aa2887d012!2s1001+Middlesex+Ave,+Port+Reading,+NJ+07064!3b1!4m7!1m0!1m5!1m1!1s0x89c3b4e9ee19ad05:0x24e963aa2887d012!2m2!1d-74.2424572!2d40.5685813" target="_blank">click here</a> for directions and details.</p>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
    <div class="accordion-group accordion-caret">
      <div class="accordion-heading"><a class="accordion-toggle" data-toggle="collapse" data-parent="#shippingAccordion" href="#collapseShippingFour"><span class="shippingMethodTitle"> International Shipping </span><span class="collapsedIcon icon pull-right"></span></a></div>
      <div id="collapseShippingFour" class="accordion-body collapse">
        <div class="accordion-inner consistentPadding">
          <div class="consistentPadding">
            <p><a href="/m/static/content/InternationalShippingPolicies">Did you ship your package to an international destination?</a></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<div class="consistentPadding">';
 UPDATE BBB_CORE_PRV.BBB_WS_ST_CONTENT SET PAGE_NAME = '200', 
 BBB_PAGE_NAME='easyReturns',
 PAGE_COPY=str,
 PAGE_TITLE='Easy Returns'  
 WHERE STATIC_TEMPLATE_ID = 'DC1bbMSSCT50006';
END;
/
DECLARE
  str varchar2(32767);
BEGIN
  str := '<style>
.alert, .alert h4 {
 color: #1a1a1a;
}
.alert {
 border: 0;
 border-left: 5px solid #fad611;
}
.alert {
 border-radius: 0px;
}
h3 {
 margin-top: 10px;
}
.bulletsReset li p {
 margin-top: 5px;
}
</style>
<p class="marBottom_20">We want you to love what you buy whether you buy it in any of our stores or online! If you are not fully satisfied with your purchase, we will help you find the item that’s right for you.</p>
<div class="alert alert-alert">
  <h3>Update to our Un-Receipted Return Policy effective April 20, 2015</h3>
  <p>Effective April 20, 2015, if you do not have a receipt for a purchase made in the last 365 days, we can look up the purchase using either your credit card or gift registry used to make the purchase.</p>
  <p>If we can''t find a record of the purchase, we will gladly complete an exchange or provide you with a merchandise credit for the current selling price less 20%. This accounts for additional discounts or coupons that may have been applied.</p>
</div>
</div>
<div class="row-fluid">
<div class="accordion accordionProduct patternGrey" id="shippingAccordion">
  <div class="accordion-group accordion-caret">
    <div class="accordion-heading"><a class="accordion-toggle" data-toggle="collapse" data-parent="#shippingAccordion" href="#collapseShippingOne1"><span class="collapsedIcon icon pull-right"></span><span class="shippingMethodTitle"> General Returns </span></a></div>
    <div id="collapseShippingOne1" class="accordion-body collapse">
      <div class="accordion-inner consistentPadding text-left">
        <div class="consistentPadding">
          <div class="bulletsReset">
            <ul>
              <li>
                <h3 class="subHeading marTop_20 returnsReceipt">Returns with a Receipt</h3>
                <p><strong>Returns with an Original Receipt</strong><br />
                  Returns can be made at any of our stores* nationwide or to our Returns Processing Center. Simply provide your original receipt or packing slip and we will complete an exchange or merchandise refund for the amount paid to the original form of payment**.</p>
                <p>*Merchandise received from a Truck Delivery cannot be returned to our stores. Please see Truck Delivery Returns.</p>
                <p>**Please see PayPal Returns for any purchase made using PayPal.</p>
                <p>**Please note that whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</p>
                <p><strong>Returns with a Gift Receipt</strong><br />
                  Merchandise returned using a Gift Receipt will either be exchanged or refunded in the form of a merchandise credit for the amount paid. Please note that whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</p>
              </li>
              <li>
                <h3 class="subHeading returnsNoReceipt">Returns without a Receipt</h3>
                <p>Don’t have a Receipt? No problem. If the purchase was made in the last 365 days, we can attempt to locate the purchase using any of the following:
                <div class="bulletsReset">
                  <ul>
                    <li>Credit/Debit Card</li>
                    <li>Store or Merchandise Credit Number</li>
                    <li>Gift Card Number</li>
                    <li>Order Number (if applicable)</li>
                    <li>Registry Number (if applicable)</li>
                    <li>Checking Account Number</li>
                  </ul>
                </div>
                </p>
                <p>If we can''t find a record of the purchase, we will gladly either:</p>
                <div class="bulletsReset">
                  <ul>
                    <li>Complete an exchange for the current selling price less 20%.</li>
                    <li>Provide a merchandise credit for the current selling price less 20%.</li>
                    <li>Complete a corporate refund request to research the original purchase.</li>
                    <p>Once the request has been completed:</p>
                    <div class="bulletsReset">
                      <ul>
                        <li>If the original purchase is located, we will complete an exchange or merchandise refund for the amount paid to the original form of payment.</li>
                        <li>If the original purchase is not located and the refund request is approved, we will complete a refund to a merchandise credit for the current selling price less 20%.</li>
                      </ul>
                    </div>
                  </ul>
                </div>
              </li>
              <li>
                <h3 class="subHeading returnsProcessing">Returns to Our Processing Center</h3>
                <p>Return postage labels are included with every purchase from our online store in the event you need to return an item. If you cannot locate the return postage label that was included with your order, please <a href="http://www.buybuybaby.com/store/selfservice/EasyReturns">click here</a> to print your free return shipping label. For returns from Military Post Office, APO, FPO and US Territories, please contact us at 1-877-3-BUY-BABY (1-877-328-9222) or <a href="mailto:customer.service@buybuybaby.com">customer.service@buybuybaby.com</a> for further instructions.</p>
                <p>Please follow the steps below to complete your return:</p>
                <div class="bulletsReset">
                  <ol>
                    <li>Please circle the item you wish to return on the bottom copy of your original packing invoice form.</li>
                    <li>Choose a reason for the return from the back of the invoice and enter its number next to your circled item.</li>
                    <li>Provide a telephone number in case we have questions.</li>
                    <li>Detach the top copy of the invoice form and keep it for your records. Enclose the bottom copy with the items you are returning.</li>
                    <li>Pack your return securely and attach the pre-paid return label found in your original carton.</li>
                    <li>To return your package you may use your regular FedEx scheduled pick up or drop off at a FedEx location (visit <a href="http://www.fedex.com/locate">fedex.com/locate</a> or call 1-800-GoFedEx (1-800-463-3339), and say “find a location”). You can schedule a Ground Pick by going to <a href="http://www.fedex.com/returnpickup">fedex.com/returnpickup</a> or call 1-800-GoFedEx and say “Return Manager”.</li>
                    <li>Should you have any questions or comments, please feel free to call us at 1-877-3-BUY-BABY (1-877-328-9222), and a customer service representative will be pleased to assist you.</li>
                  </ol>
                </div>
                <p>You may also mail returns via the US Postal Service to the address below. We recommend that you insure the package prior to shipment. Enclosing your original packing invoice will help us expedite your return.</p>
                <ul>
                  <li class="address">buybuy BABY</li>
                  <li class="address">Returns Processing / Door 39</li>
                  <li class="address">1001 West Middlesex Avenue</li>
                  <li class="address">Port Reading, New Jersey 07064 </li>
                </ul>
              </li>
              <li>
                <h3 class="subHeading">Note for All Returns</h3>
                <div class="bulletsReset">
                  <ol>
                    <li>Merchandise cannot be used and must be in the same condition/packaging in which you received it.</li>
                    <li>Breast pumps may only be returned unopened and require proof of purchase.</li>
                    <li>Baby clothing and Destination Maternity items may only be returned with all tags intact and require proof of purchase. Returns of these items made more than 90 days after the purchase date will receive a merchandise credit for the current selling price.</li>
                    <li>Shipping, delivery and assembly charges are non-refundable.</li>
                    <li>All gift cards are non-refundable</li>
                    <li>Whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</li>
                    <li>Please see PayPal Returns for any purchase made using PayPal.</li>
                    <li>Please see Truck Delivery Returns for additional return restrictions.</li>
                  </ol>
                </div>
                <p>To return merchandise to a store, please <a href="/m/storeLocator">click here</a> to find the one closest to you. To return merchandise to our Returns Processing Center, <a href="https://www.google.com/maps/dir//1001+Middlesex+Ave,+Port+Reading,+NJ+07064/@40.5685813,-74.2424572,17z/data=!4m13!1m4!3m3!1s0x89c3b4e9ee19ad05:0x24e963aa2887d012!2s1001+Middlesex+Ave,+Port+Reading,+NJ+07064!3b1!4m7!1m0!1m5!1m1!1s0x89c3b4e9ee19ad05:0x24e963aa2887d012!2m2!1d-74.2424572!2d40.5685813" target="_blank">click here</a> for directions and details.</p>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="accordion-group accordion-caret">
    <div class="accordion-heading"><a class="accordion-toggle" data-toggle="collapse" data-parent="#shippingAccordion" href="#collapseShippingTwo2"><span class="shippingMethodTitle"> Paypal Returns</span><span class="collapsedIcon icon pull-right"></span></a></div>
    <div id="collapseShippingTwo2" class="accordion-body collapse">
      <div class="accordion-inner consistentPadding">
        <div class="consistentPadding">
          <div class="bulletsReset">
            <ul>
              <li>
                <h3 class="subHeading marTop_20 paypalStores">To Our Stores</h3>
                <p>Merchandise paid for with PayPal will be refunded in the form of a Merchandise Credit.</p>
              </li>
              <li>
                <h3 class="subHeading paypalCenter">To Our Returns Center</h3>
                <p>Merchandise paid for with PayPal will be refunded back to PayPal. Please see below regarding shipping fees.</p>
              </li>
              <li>
                <h3 class="subHeading">Note for All Returns</h3>
                <div class="bulletsReset">
                  <ol>
                    <li>Merchandise cannot be used and must be in the same condition/packaging in which you received it.</li>
                    <li>Breast pumps may only be returned unopened and require proof of purchase.</li>
                    <li>Baby clothing and Destination Maternity items may only be returned with all tags intact and require proof of purchase. Returns of these items made more than 90 days after the purchase date will receive a merchandise credit for the current selling price.</li>
                    <li>Shipping, delivery and assembly charges are non-refundable.</li>
                    <li>All gift cards are non-refundable</li>
                    <li>Whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</li>
                    <li>Please see PayPal Returns for any purchase made using PayPal.</li>
                    <li>Please see Truck Delivery Returns for additional return restrictions.</li>
                  </ol>
                </div>
                <p>To return merchandise to a store, please <a href="/m/storeLocator">click here</a> to find the one closest to you. To return merchandise to our Returns Processing Center, <a href="https://www.google.com/maps/dir//1001+Middlesex+Ave,+Port+Reading,+NJ+07064/@40.5685813,-74.2424572,17z/data=!4m13!1m4!3m3!1s0x89c3b4e9ee19ad05:0x24e963aa2887d012!2s1001+Middlesex+Ave,+Port+Reading,+NJ+07064!3b1!4m7!1m0!1m5!1m1!1s0x89c3b4e9ee19ad05:0x24e963aa2887d012!2m2!1d-74.2424572!2d40.5685813" target="_blank">click here</a> for directions and details.</p>
              </li>
              <a name="truck"></a>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="accordion-group accordion-caret">
    <div class="accordion-heading"><a class="accordion-toggle" data-toggle="collapse" data-parent="#shippingAccordion" href="#collapseShippingThree"><span class="shippingMethodTitle"> Truck Delivery Returns</span><span class="collapsedIcon icon pull-right"></span></a></div>
    <div id="collapseShippingThree" class="accordion-body collapse">
      <div class="accordion-inner consistentPadding">
        <div class="consistentPadding">
          <p>We want you to enjoy your purchase for years to come. That’s why it’s important to understand what to do in the event that you have an issue with your purchase.</p>
          <div class="bulletsReset marLeft_10">
            <ul>
              <li>
                <h3 class="subHeading truckAccept">Accepting Your Truck Delivery</h3>
                <p>Someone over the age of 18 must be present to sign for your delivery. Also, please note that once your delivery is scheduled and confirmed, if you are not home to accept the delivery, you will be responsible for all shipping, storage and vendor charges (such as cancellation or restocking fees) as specified in the chart below.</p>
                <p class="bold">While the delivery person is present AND prior to signing for delivery:</p>
                <div class="bulletsReset">
                  <ul>
                    <li>Open and inspect the item(s).</li>
                    <li>For any damage (even minimal), write “Package Damaged” on the delivery form.</li>
                    <li>Call 844-4BBBHOME (844-422-2466) immediately to report any defects or damages.</li>
                  </ul>
                </div>
                <p class="bold">After Accepting Your Delivery</p>
                <div class="bulletsReset">
                  <ol>
                    <li>Within 48 Hours<br>
                      If you notice missing or damaged parts, call 844-4BBBHOME (844-422-2466) within 48 hours and we will send you replacement parts free of charge.</li>
                    <li>Within 30 Days<br>
                      Item(s) may be returned for any reason as long as the following conditions are met:
                      <ul>
                        <li>The item(s) must be in its original condition.</li>
                        <li>Truck Delivery purchases may NOT be returned to a buybuy BABY store. Our stores are not equipped to handle these types of returns.</li>
                        <li>Customer is responsible for all shipping, storage and vendor charges (such as cancellation or restocking fees) as specified in the Chart below.</li>
                      </ul>
                      <p>To schedule a return within 30 days under these conditions, call our Delivery Customer Service team at 844-4BBBHOME (844-422-2466) between the hours of 8:00 a.m. to 11:00 p.m. Monday – Saturday (Eastern Time). We will make arrangements to have our carrier contact you to schedule a pick up of your return.</p>
                    </li>
                    <li>After 30 Days<br>
                      Returns will not be accepted after 30 days. For issues after 30 days, please contact the manufacturer.</li>
                  </ol>
                </div>
              </li>
              <li>
                <h3 class="subHeading truckCancel">Cancellation &amp; Return Schedule Fee Chart</h3>
                <table width="976" cellpadding="5" id="LTLTable">
                  <tr class="noBorder">
                    <td class="bold FS15">Cancellation &amp; Return Schedule</td>
                    <td class="bold FS15">Fee</td>
                  </tr>
                  <tr>
                    <td>Orders Cancelled within 24 hours of purchase</td>
                    <td>No Fee</td>
                  </tr>
                  <tr>
                    <td>Orders Cancelled after 24 hours of purchase and before Shipper contact</td>
                    <td>Restocking fee (up to 20% of purchase price)</td>
                  </tr>
                  <tr>
                    <td>Orders Cancelled after Shipper Contact, at Delivery, or after Delivery</td>
                    <td>Restocking fee (up to 20% of purchase price) + Cost of Return Freight</td>
                  </tr>
                </table>
                <p>The restocking fee covers administrative, shipping, storage, and all associated fees incurred during the processing and shipping of your order.</p>
              </li>
              <li>
        <h3 class="subHeading mattressReturnsHeading">Adult Size Mattress Returns: What You Should Know</h3>
        <p>The transition from your old mattress to a new mattress can feel very different. In many cases, it can take about 30 days for your body to adjust to your new mattress. If after 30 days you are not completely satisfied, we will allow a one-time exchange up to 120 days from delivery.</p>
        <p class="bold">Please Note:<br>
          Mattresses can only be exchanged:</p>
        <div class="bulletsReset">
          <ul class="mattress">
            <li>Within 120 days of delivery,</li>
            <li>Are not eligible to be returned</li>
            <li>By calling 1-844-4BBBHOME (844-422-2466) and cannot be exchanged at any buybuy BABY store. Our stores do not sell mattresses and they are not equipped to handle these types of exchanges.</li>
          </ul>
        </div>
        <p class="bold">If you decide to exchange your mattress, only the purchase price of the mattress will be applied to your exchange.<br>
          Customers are responsible for:</p>
        <div class="bulletsReset">
          <ul class="mattress noMarBot">
            <li>Original shipping charges</li>
            <li>Return shipping charges</li>
            <li>Vendor surcharges (such as cancellation or restocking fees), and</li>
            <li>Shipping charges for the new mattress</li>
          </ul>
        </div>
        <p class="noMarTop">To schedule an exchange, call our Home Delivery team at 1-844-4BBBHOME (844-422-2466) between the hours of 8:00 a.m. and 11:00 p.m. Monday – Saturday (Eastern Time).</p>
        </li>
              <li>
                <h3 class="subHeading">Note for All Returns</h3>
                <div class="bulletsReset">
                  <ol>
                    <li>Merchandise cannot be used and must be in the same condition/packaging in which you received it.</li>
                    <li>Breast pumps may only be returned unopened and require proof of purchase.</li>
                    <li>Baby clothing and Destination Maternity items may only be returned with all tags intact and require proof of purchase. Returns of these items made more than 90 days after the purchase date will receive a merchandise credit for the current selling price.</li>
                    <li>Shipping, delivery and assembly charges are non-refundable.</li>
                    <li>All gift cards are non-refundable</li>
                    <li>Whenever a Merchandise Credit is issued, customers will be asked to provide a government issued ID. This will help ensure if a Merchandise Credit is ever lost, we can research and/or replace.</li>
                    <li>Please see PayPal Returns for any purchase made using PayPal.</li>
                    <li>Please see Truck Delivery Returns for additional return restrictions.</li>
                  </ol>
                </div>
                <p>To return merchandise to a store, please <a href="/m/storeLocator">click here</a> to find the one closest to you. To return merchandise to our Returns Processing Center, <a href="https://www.google.com/maps/dir//1001+Middlesex+Ave,+Port+Reading,+NJ+07064/@40.5685813,-74.2424572,17z/data=!4m13!1m4!3m3!1s0x89c3b4e9ee19ad05:0x24e963aa2887d012!2s1001+Middlesex+Ave,+Port+Reading,+NJ+07064!3b1!4m7!1m0!1m5!1m1!1s0x89c3b4e9ee19ad05:0x24e963aa2887d012!2m2!1d-74.2424572!2d40.5685813" target="_blank">click here</a> for directions and details.</p>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
    <div class="accordion-group accordion-caret">
      <div class="accordion-heading"><a class="accordion-toggle" data-toggle="collapse" data-parent="#shippingAccordion" href="#collapseShippingFour"><span class="shippingMethodTitle"> International Shipping </span><span class="collapsedIcon icon pull-right"></span></a></div>
      <div id="collapseShippingFour" class="accordion-body collapse">
        <div class="accordion-inner consistentPadding">
          <div class="consistentPadding">
            <p><a href="/m/static/content/InternationalShippingPolicies">Did you ship your package to an international destination?</a></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<div class="consistentPadding">';
  UPDATE BBB_CORE_PRV.BBB_WS_ST_CONTENT SET PAGE_NAME = '200', 
 BBB_PAGE_NAME='easyReturns',
 PAGE_COPY=str,
 PAGE_TITLE='Easy Returns'  
 WHERE STATIC_TEMPLATE_ID = 'DC1bbMSSCT7014';
END;
/

---- insert mobileSiteStaticContent : BBB_CORE_PRV.BBB_WS_ST_CONTENT

INSERT INTO BBB_CORE_PRV.BBB_WS_ST_CONTENT (STATIC_TEMPLATE_ID,PAGE_NAME,BBB_PAGE_NAME,PAGE_COPY,PAGE_TITLE) values ('DC1bbMSSCT1500001','200','GiftPackagingUnavailable','<div class="grid_8 cb"> 
     <div class="bulletsReset">
      <ul>
                                                                              <li>This is the new content </li>
       <li>Due to nature of this item, gift packaging will not be available</li></strong>
       <li>We apologize for any incovenience this might have caused.</li>
      </ul>
     </div>
    </div>','Gift Packaging Unavailable');
	
---- insert mobileSiteStaticContent : BBB_CORE_PRV.BBB_WS_STATIC_SITE

INSERT INTO BBB_CORE_PRV.BBB_WS_STATIC_SITE (STATIC_TEMPLATE_ID,SITE_ID) values ('DC1bbMSSCT1500001','BedBathCanada');
INSERT INTO BBB_CORE_PRV.BBB_WS_STATIC_SITE (STATIC_TEMPLATE_ID,SITE_ID) values ('DC1bbMSSCT1500001','BedBathUS');
INSERT INTO BBB_CORE_PRV.BBB_WS_STATIC_SITE (STATIC_TEMPLATE_ID,SITE_ID) values ('DC1bbMSSCT1500001','BuyBuyBaby');

--- insert registry input : BBB_CORE_PRV.BBB_REG_INPUT

INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500028','0','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300001','0','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500039','0','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500050','0','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500040','1','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500029','1','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500018','1','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500001','1','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400010','1','','','','');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500019','2','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500041','2','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500030','2','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1200001','2','1','1','1','1');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400015','3','1','1','1','1');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500031','3','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500042','3','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500020','3','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1200002','3','1','1','0','1');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500043','4','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400016','4','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500032','4','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500021','4','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400013','6','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400019','6','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500049','6','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400014','6','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500054','6','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400002','6','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400022','6','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300002','6','1','0','0','1');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500006','6','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400018','6','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1200004','7','1','1','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400004','7','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400020','7','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400021','7','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400003','7','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500005','7','1','1','1','1');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400017','7','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400007','7','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1200005','8','1','1','0','1');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500044','8','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500022','8','1','1','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500033','8','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500007','8','1','1','1','1');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500008','9','1','1','1','1');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500034','9','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500045','9','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500023','9','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1200006','10','1','0','0','1');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500010','10','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500046','10','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500026','10','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500047','11','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500009','11','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500024','11','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500035','11','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500017','12','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1600001','13','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1400006','13','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500014','14','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500015','15','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500016','16','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500027','17','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500037','17','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500012','17','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300017','18','1','1','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300003','18','1','1','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300005','18','1','1','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300019','18','1','1','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300022','18','1','1','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300011','18','1','1','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300013','18','1','1','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300008','18','1','1','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300015','18','1','1','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500013','18','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500011','19','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500025','19','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500036','19','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1500048','19','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1600003','20','1','1','1','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('Wedding_eventDate','13','1','1','1','1');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300004','21','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300010','21','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300016','21','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300014','21','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300012','21','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300006','21','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300020','21','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300009','21','1','0','0','0');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUT (ID,FIELD_NAME,DISPLAY_ON_FORM,REQUIRED_INPUT_CREATE,REQUIRED_INPUT_UPDATE,REQUIRED_FOR_PUBLIC) values ('DC1300018','21','1','0','0','0');

---- INSERT REGISTRY INPUTS BY TYPE : BBB_CORE_PRV.BBB_REGINPUTS_BY_REGTYPE

INSERT INTO BBB_CORE_PRV.BBB_REGINPUTS_BY_REGTYPE (ID,EVENT_TYPE,ISPUBLIC) values ('DC1700004','Anniversary','1');
INSERT INTO BBB_CORE_PRV.BBB_REGINPUTS_BY_REGTYPE (ID,EVENT_TYPE,ISPUBLIC) values ('DC1500001','Baby','1');
INSERT INTO BBB_CORE_PRV.BBB_REGINPUTS_BY_REGTYPE (ID,EVENT_TYPE,ISPUBLIC) values ('DC1700006','Birthday','1');
INSERT INTO BBB_CORE_PRV.BBB_REGINPUTS_BY_REGTYPE (ID,EVENT_TYPE,ISPUBLIC) values ('DC1700005','College/University ','1');
INSERT INTO BBB_CORE_PRV.BBB_REGINPUTS_BY_REGTYPE (ID,EVENT_TYPE,ISPUBLIC) values ('DC1700002','Commitment Ceremony','1');
INSERT INTO BBB_CORE_PRV.BBB_REGINPUTS_BY_REGTYPE (ID,EVENT_TYPE,ISPUBLIC) values ('DC1700003','Housewarming','1');
INSERT INTO BBB_CORE_PRV.BBB_REGINPUTS_BY_REGTYPE (ID,EVENT_TYPE,ISPUBLIC) values ('DC1700008','Other','1');
INSERT INTO BBB_CORE_PRV.BBB_REGINPUTS_BY_REGTYPE (ID,EVENT_TYPE,ISPUBLIC) values ('DC1700007','Retirement','1');
INSERT INTO BBB_CORE_PRV.BBB_REGINPUTS_BY_REGTYPE (ID,EVENT_TYPE,ISPUBLIC) values ('DC1500002','Wedding','1');

--- INSERT REGiNPUTlIST : BBB_CORE_PRV.BBB_REG_INPUTLIST

INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1300022');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1500048');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1500046');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1500045');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1500044');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1500035');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1500043');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1500037');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1500042');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1500050');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1500041');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1400007');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1600001');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1500001');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500023');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500022');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500021');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500020');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1300001');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500027');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500026');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500019');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500025');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500018');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500024');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1300002');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500017');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500015');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500016');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1500014');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1300003');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500001','DC1300004');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1500048');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1500047');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1500046');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1500045');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1500044');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1500043');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1500042');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1400017');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1500041');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1500040');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1600001');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1500039');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1300018');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700006','DC1300019');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700005','DC1500023');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700005','DC1200006');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700005','DC1500033');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700005','DC1500025');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700005','DC1500024');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700005','DC1600003');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700005','DC1500028');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700005','DC1500029');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700005','DC1600001');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700005','DC1300009');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1500032');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1500031');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1500034');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1500033');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1500036');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1500035');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1500006');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1500037');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1500030');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1400003');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1500028');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1500029');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1300011');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700002','DC1500029');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1500034');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1500046');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1500036');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1500044');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1500043');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1500042');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1500037');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1500041');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1500040');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1400007');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1600001');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1300017');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1500039');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700003','DC1300016');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700004','DC1300020');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700005','DC1300008');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700007','DC1300013');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1500042');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700009','DC1500041');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700010','DC1500040');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700011','DC1400007');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700012','DC1600001');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700013','DC1500039');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1500046');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1500045');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1500044');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1500035');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1500043');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1500050');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1500041');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1400007');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1600001');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1500029');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1500011');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1300014');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700008','DC1300015');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700007','DC1500034');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700007','DC1500046');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700007','DC1500035');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700007','DC1500044');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700007','DC1500025');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700007','DC1500043');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700007','DC1500042');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700007','DC1400017');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700007','DC1500050');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700007','DC1500041');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700007','DC1600001');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1700007','DC1300012');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1300001');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1500007');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1500006');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1500009');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1500008');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1400016');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1200001');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1400015');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1500013');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1500005');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1500011');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1500012');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1500001');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1500010');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','Wedding_eventDate');
INSERT INTO BBB_CORE_PRV.BBB_REG_INPUTLIST (ID,REG_INPUTLIST_ID) values ('DC1500002','DC1300006');

--- insert staticTemplateData : BBB_CORE_PRV.BBB_Static_Template


DECLARE
  str varchar2(32767);
BEGIN
  str := '<div id="cmsPageContent">
<div class="grid_12 clearfix"><p><b>Privacy Policy Effective Date: This Privacy Policy became effective in 1999, and was most recently changed on November 10, 2010.</b></p>
<p>At buybuy BABY we are committed to protecting the privacy of our customers, and therefore do not sell or rent our customers’ Personal Information to any third party. Further, we do not share your Personal Information with any third parties for their direct marketing purposes without first providing you the opportunity to opt-in or opt-out of such sharing. You may request information about how we share information consistent with this Privacy Policy by contacting us at: <a href="mailto:privacy.information@buybuybaby.com">privacy.information@buybuybaby.com</a>.</p>
<p>The www.buybuybaby.com web site (the <b>&quot;Site&quot;</b>) is owned by Buy Buy Baby, Inc.. We refer to Buy Buy Baby, Inc. as the &quot;Company,&quot; &quot;<b>we</b>,&quot; &quot;<b>us</b>&quot; or &quot;<b>our</b>.&quot; The Company’s parent, Bed Bath & Beyond Inc. (&quot;Bed Bath & Beyond&quot;), assists with the operation and maintenance of the Site.</p>
<p>This page sets forth our policies regarding the use and disclosure of information we collect from or are able to obtain about the Site’s users. We refer to these policies as our &quot;<b>Privacy Policy</b>.&quot; In addition to the lower case words &quot;we&quot; &quot;us&quot; and &quot;our&quot; defined above, you will notice that we capitalize certain words in this Privacy Policy even though the rules of grammar do not require that they be capitalized. We do this because these words have specific meanings in this Privacy Policy. These specific meaning are found where the capitalized words are placed in bold text and inside quotation marks.</p>
<p>We hope this Privacy Policy will answer your questions regarding how we treat information gathered from our Site. If you have questions or concerns regarding this Privacy Policy, other than requests for our information sharing practices as described in the first paragraph above, please feel free to contact us at <a href="mailto:customer.service@buybuybaby.com">customer.service@buybuybaby.com</a>; or, if you wish to exclude your Personal Information from our direct marketing programs including e-mail, please refer to the section titled &quot;How to Opt-Out,&quot; below.</p>
<p>This Privacy Policy does not apply to information that you may provide to us, or that we may obtain through means other than the Site, such as by phone or postal mail, but please note that Personal Information obtained through such means will be treated in accordance with the first paragraph above and our use of such Personal Information will be consistent with this Privacy Policy. By accessing and using the Site you are agreeing to the terms of this Privacy Policy. If you do not agree to the terms of this Privacy Policy, you are not authorized to use the Site. Furthermore, by providing us with Personal Information on this Site, you fully understand and unambiguously consent to the collection and processing of such information in the United States in accordance with this Privacy Policy.</p>
<p>As we update and diversify our products and services, this Privacy Policy may change. We reserve the right, at our discretion, to change, modify, add, or remove portions of this Privacy Policy at any time. Please check this page for updates periodically. If we make any material changes to this Privacy Policy, we will notify you here. Unless the law otherwise requires, we do not provide notice of changes to this Privacy Policy and your use of the Site following any posted changes will indicate your acceptance of such changes. <b>This Privacy Policy became effective in 1999, and was most recently changed on November 10, 2010.</b> The last change to this policy was:</p>
<ul>
<li>Clarification on our use of third party advertisers. </li>
<li>Clarification regarding the use of Personal Information for processing returns and exchanges, and for protection against fraud and error. </li>
</ul>
<h3>What Information We Actively Collect and Inactively Gather</h3>
<h3>Personal Information </h3>
<p>In order to process orders efficiently, when you place an order with us or register an account on the Site we actively collect the following personal information: name, billing address, shipping address, e-mail address, phone number, and credit card number (&quot;<b>Personal Information</b>&quot;). If you prefer not to disclose your Personal Information online, you can always place your order by calling 1-877-3-BUY-BABY (1-877-328-9222) or visiting one of our many stores.</p>
<p>We may periodically conduct surveys of visitors to our Site for internal research purposes that may include marketing. This helps us better understand what our customers would like to see from us. If you respond to such surveys, we may ask you for some Personal Information and/or may contact you for more information, but only if you have given us permission to do so.</p>
<p>We may also receive information about you from other sources.</p>
<h3>Site Usage Information</h3>
<p>We (including through our third party service providers) may inactively collect from you information that is not in the same category as Personal Information. For example, we might track information about the date and time you access the Site, the type of web browser you used, and the web site from which you connect to our Site. We collect and use this and other non-Personal Information (such as demographic data) to analyze and develop our business strategy and to determine how you found out about us, your interests regarding our products and services and how to further improve our Site. We, or a third party, may also collect information from your activities on the Site as well as other sites for the purpose of providing advertisements and other content that is customized to your interests and preferences. This means that our ads may appear on participating web sites based upon your browsing activity. To accomplish all this, and also to allow us to offer our customers streamlined ordering and other useful features, we, as do many web sites, use and allow our third party advertising companies to use the technologies listed below and other similar technologies (the &quot;<b>Tracking Technologies</b>&quot;): </p>
<ul>
<li><b>Cookies and Clear GIFs</b> - Cookies are pieces of information that are stored by your browser on your computer’s hard drive. Cookies make web surfing easier for you by saving your customer identification information (i.e., username, password, shopping cart information, etc.) while you’re at a site. Cookies also track where you travel on a site and what you look at and purchase. A clear GIF is typically a one-pixel, transparent image (although it can be a visible image as well), located on a web page or in an e-mail or other type of message, which is retrieved from a remote site on the Internet enabling the verification of an individual’s viewing or receipt of a web page or message. You can learn more about how third party advertising companies use cookies and clear GIFs, including how to opt-out, by clicking <a href="http://www.networkadvertising.org/choices" target="_blank">here</a>. Please read the explanation carefully should you choose to opt-out using this method.</li>
<li><b>IP Address Tracking and Clickstream Data</b> - In addition to cookies and clear GIFs, our servers automatically collect data about your server’s Internet address when you visit our Site. This information, known as an Internet Protocol address, or &quot;<b>IP Address</b>&quot;, is a number that is automatically assigned to your computer by your Internet service provider whenever you are on the Internet. When you view pages from our Site, our servers may record or &quot;log&quot; your IP Address and sometimes your domain name. Our server also may record the page that linked you to us and related information including any ads you may have clicked on. Such information is known as &quot;<b>Clickstream Data</b>&quot;. </li>
<li><b>Email Interconnectivity</b> - If you receive email from us, we may use certain tools to capture data related to when you open our message, click on any links or banners it contains and make purchases. </li>
</ul>
<p>In some cases, we may link the non-Personal Information collected through the Tracking Technologies with certain of your Personal Information. WE WANT TO BE SURE THAT YOU UNDERSTAND THAT ACCEPTING TRACKING TECHNOLOGIES ONTO YOUR COMPUTER IN NO WAY GIVES US ACCESS TO YOUR COMPUTER OR ANY PERSONAL INFORMATION ABOUT YOU. YOU MAY SET YOUR WEB BROWSER SOFTWARE TO REJECT TRACKING TECHNOLOGIES, BUT, IF YOU DO SO, CERTAIN FUNCTIONALITY OF THE SITE MAY BE AFFECTED. Third parties (including, but not limited to, software vendors, advertisers and fulfillment companies) may also use Tracking Technologies by or through the Site. WE HAVE NO CONTROL OVER WHAT INFORMATION SUCH THIRD PARTIES TRACK OR COLLECT, AND HAVE NO RESPONSIBILITY OR LIABILITY FOR ANY TRACKING, DATA COLLECTION OR OTHER ACTIVITIES OF SUCH THIRD PARTIES. </p>
<h3>Mobile Device Identifiers</h3>
<p>You may be visiting the Site from your mobile device. Certain mobile service providers uniquely identify mobile devices and we or our third-party service providers may receive such information if you access the Site through mobile devices. Some features of the Site may allow for the collection of mobile phone numbers and we may associate that phone number to mobile device identification information. Furthermore, some mobile phone service providers operate systems that pinpoint the physical location of devices that use their service. Depending on the provider, we or our third-party service providers may receive this information.</p>
<h3>Wireless Addresses</h3>
<p>If an e-mail address you have provided us is a wireless e-mail address, you agree to receive messages at such address from us (unless and until you have elected not to receive such messages by following the &quot;How to Opt-Out&quot; instructions below). You understand that your wireless carrier’s standard rates apply to these messages, and that you may change your mind at any time by following the instructions below. You represent that you are the owner or authorized user of the wireless device on which messages will be received, and that you are authorized to approve the applicable charges.</p>
<h3>Security of the Information You Provide</h3>
<p>Among our top priorities is keeping your Personal Information secure. We use Secure Sockets Layer (SSL) technology to encrypt all of your Personal Information before it is transmitted to us so that it can be safeguarded as much as possible from being read or recorded as it travels over the Internet. You can tell you are sending information securely by the unbroken key icon or closed lock icon that appears at the bottom of your Internet browser''s window. These icons will appear when you are placing an order on the Site. In addition, you will see the Site address change slightly, from http: to https: indicating a secure server connection is being used. The computers that store your Personal Information are located in a separate facility which employs firewall and security technology. We employ these procedures to protect your Personal Information from unauthorized access, destruction, use, modification or disclosure.</p>
<p>You may be able to create an account on our Site with a username and password, including if you set up a customer account or a registry. We recommend you exercise extreme care in protecting your password information and do not publicly share any of your passwords. You are responsible for maintaining the strict confidentiality of your account password, and you are responsible for any access to or use of the Site by you or another person or entity using your password, whether or not such access or use has been authorized by or on behalf of you.</p>
<p>Your Personal Information will be stored and processed on our computers in the United States. The laws on holding Personal Information in the United States may vary and be less stringent than laws of your state or country. We will use commercially reasonable efforts to hold and transmit your Personal Information in a safe, confidential and secure environment. If you object to your Personal Information being transferred or used in this manner please do not register with or use the Site.</p>
<p>ALTHOUGH WE WILL TAKE (AND REQUIRE OUR THIRD-PARTY PROVIDERS TO TAKE) REASONABLE SECURITY PRECAUTIONS TO PROTECT THE PERSONAL INFORMATION COLLECTED FROM AND STORED ON THE SITE, BECAUSE OF THE OPEN NATURE OF THE INTERNET, WE CANNOT GUARANTEE THAT PERSONAL INFORMATION STORED ON OUR SERVERS, TRANSMITTED TO OR FROM A USER, OR OTHERWISE IN OUR CARE WILL BE ABSOLUTELY SAFE FROM INTRUSION BY OTHERS, SUCH AS HACKERS. ACCORDINGLY, WE DISCLAIM ANY LIABILITY FOR ANY THEFT OR LOSS OF, UNAUTHORIZED ACCESS OR DAMAGE TO, OR INTERCEPTION OF ANY DATA OR COMMUNICATIONS. BY USING THE SITE, YOU ACKNOWLEDGE THAT YOU UNDERSTAND AND AGREE TO ASSUME THESE RISKS.</p>
<h3>How Do We Use the Information that You Provide and We Collect?</h3>
<p>We collect Personal Information for the purposes of fulfilling orders, processing returns and exchanges and providing you with the services you have chosen, providing a high level of customer service, providing you with information we believe may be of interest including mailing our circulars and special offers, for editorial and feedback purposes, for internal marketing and promotional purposes, for a statistical analysis of your behavior, for product development, for content improvement, for protection against fraud and error and to customize the content and layout of the Site (collectively, &quot;<b>Information Usage Purposes</b>&quot;). Notwithstanding the foregoing, we will not knowingly use a mobile phone number for marketing purposes without first obtaining your consent. In performing the Information Usage Purposes, we may combine information we collect on the Site with information we receive from other sources. We do not sell Personal Information and we do not disclose Personal Information to any other party, except as set forth in this Privacy Policy. Unless otherwise provided in this Privacy Policy, any non-Personal Information you transmit to the Site or we collect from the Site will be treated as non-confidential and non-proprietary and you agree that we may use any such non-Personal Information (together with any ideas, concepts, know-how, or techniques contained therein) in any manner, for any purpose, without credit or compensation to you. Without limiting the generality or application of the foregoing, the information that you publish on or through the Baby Registry is governed by specific terms available below under the heading &quot;Baby Registry Information&quot;.</p>
<h3>Sharing of Your Information With Our Partners; Transfer of Your Information</h3>
<p>We may provide your Personal Information to our associates and to companies acting as our authorized agents or partners in providing our service or co-branded services to you and in performing Information Usage Purposes. This would include our parent company, Bed Bath & Beyond, but only to the extent it performs or assists in the performance of Information Usage Purposes on our behalf. We screen and reasonably determine to be reputable any third party to whom we disclose such information and we require such third parties to agree to use the Personal Information only for such specified purposes. Each of our partners must agree to implement and maintain reasonable security procedures and practices appropriate to the nature of your Personal Information in order to protect your Personal Information from unauthorized access, destruction, use, modification or disclosure.</p>
<p>If we merge, or sell or otherwise transfer our assets, Site or operations, we may disclose or transfer your Personal Information in connection with such transaction.</p>
<h3>Baby Registry Information</h3>
<p>Baby Registry Information is treated differently from information submitted for other purposes on our Site. Registry users may receive additional e-mail communications from us, such as a registry welcome e-mail and follow-up contact after the baby’s expected arrival date. In addition, cookies are used as part of our registry service to save your registry number on your computer. Finally, by registering with us and opting to make your registry available online, your registry information, which includes your name, the state where you live and your baby’s expected arrival date, will be made available to our partners that help provide our services and our co-branded services (including for Information Usage Purposes) as well as search engines and spiders that are not affiliated with us.</p>
<p>Finally, and to illustrate the type of business partner with whom your registry information may be shared, if you have not taken advantage of the applicable opt-out procedure, your registry information (your name, state where you live and baby’s expected arrival date) may be made available to third party gift registry portal sites. We may also share the e-mail address associated with your registry, but solely to enable us and such a third-party registry portal site to link your purchases or other activity with the registry you have established with us. The use of such portal sites is intended to provide your guests with additional means of finding your registry, and to provide you with additional ways to create a registry with us. Any third-party registry portal site with whom an e-mail address is shared as a registry identifier is contractually prohibited from using it for any other purpose not outlined above. In the future, if you wish to opt-out of our sharing your information with such sites, please send an e-mail to <a href="mailto:customer.service@buybuybaby.com">customer.service@buybuybaby.com</a> clearly indicating such request.</p>
<p>Please review the Site’s Terms and Conditions for more information on the use of the Baby Registry.</p>
<h3>Use of Information in Sweepstakes and Contests</h3>
<p>Occasionally, we may run a promotion such as a sweepstakes or a contest (a &quot;Promotion&quot;), and you will be given the opportunity to enter by filling out an entry form, or in some cases, you may be automatically entered by using a designated type of credit card when placing an order in store, online or via phone or, for example, by creating a Baby Registry. Your entry information may be shared with third parties for the limited purposes of administering the sweepstakes and selecting a winner(s) and may be used to notify you in connection with the sweepstakes or contest and to verify your identity. When you enter such a Promotion, you will have the opportunity to consent to receive future communications from us. Since we value your privacy, you will always have the opportunity to stop such communications by opting-out, as detailed above. In addition, some Promotions may have one or more co-sponsors, and you may also be asked upon entry to consent to receive future communications from such co-sponsors. In the event your consent is indicated, the information from your entry will be shared with the co-sponsors. In those situations, the third parties will have the right to use your information for their own purposes, in accordance with their own policies, and we are not responsible for how co-sponsors may use your information. In all events, we will make it clear to you in the entry, order or registration form, before you submit it, whether you are consenting to receive information from any party, and you will be given the opportunity to confirm or withdraw such consent prior to submitting your entry, order or registration.</p>
<p>In any case, where you elect to have us share your information with third parties, such as in the Promotions discussed above, those elections to have your information shared will supersede anything to the contrary in this Privacy Policy. </p>
<h3>How to Opt-out</h3>
<p>We will only send e-mail for customer service purposes and to those customers who have indicated that they wish to receive other types of e-mail from us. </p>
<p>If at any time you wish to stop receiving e-mail from us, you can always remove your name from our mailing list by sending an e-mail to <a href="mailto:customer.service@buybuybaby.com">customer.service@buybuybaby.com</a> stating that you wish to unsubscribe and indicating in the subject line of the e-mail "NO E-MAIL". Although we will promptly remove your name from our e-mail list upon receiving your request, it is possible that you may still receive e-mails from us that had been initiated prior to your name being removed from our list.</p>
<p>If at any time you do not want to receive offers and/or circulars from us, you can always remove yourself from our mailing list by sending an e-mail to <a href="mailto:customer.service@buybuybaby.com">customer.service@buybuybaby.com</a>, indicating "NO SNAIL MAIL" in the subject line along with your name, address and zip code. Please note that our mailings are prepared in advance of their being sent. Although we will promptly remove your name from our mailing list upon receiving your request, you may still receive mailings from us that had been initiated prior to your name being removed from our list. You can also contact us by mail:</p>
<center>
    <table>
     <tr>
      <td>
      Buy Buy Baby, Inc.<br>
      Attention: Customer Service - Privacy<br>
      Internet Division<br>
      650 Liberty Avenue<br>
      Union, NJ 07083<br>
      customer.service@buybuybaby.com<br>
      </td>
     </tr>
    </table>
   </center>
<p>THE OPT-OUT RIGHTS AND PROCEDURES DESCRIBED IN THIS SECTION ARE THE DEFAULT RULES AND PROCEDURES WE USE IN OPERATING THE SITE AND ARE THE STANDARD BY WHICH WE ADHERE TO OUR PRIVACY POLICY.</p>
<h3>Release of Information to Protect Our Site and Others</h3>
<p>We may release Personal Information when we believe, in good faith, that such release is necessary to comply with law, protect the rights, properties or safety of the Company, our users or others, or enforce or apply the terms of any of our user agreements.</p>
<h3>Your Account Access Rights</h3>
<p>You may review and make changes to the Personal Information that is stored in your user account on the Site by visiting the &quot;My Account&quot; area of the Site.</p>
<h3>Intended Users; Use by Children under 13</h3>
<p>The Site is not purposefully directed at children under the age of 13 and all products available for sale on the Site are intended to be purchased by adults. We do not collect or maintain Personal Information, via the Site or otherwise, from those we actually know are children under 13 years of age. If you notify us that information has been provided by a child under the age of 13, upon verification, we will promptly delete such information.</p>
<h3>Third Party Ad Servers; Links to Other Web Sites</h3>
<p>We may use third-party advertising companies to serve ads when you visit our Site. The Site also may contain links to other web sites that are not under our control. These links may include the use of so-called &quot;share links.&quot; A &quot;share link&quot; is a button and/or text link appearing on our Site that enables the launch of a sharing mechanism whereby you can post links to, and content from, the Site onto social networking web sites such as Facebook. The privacy policies of other web site operators or advertisers may significantly differ from our Privacy Policy. We urge you to read the privacy statements of each and every web site you visit and to contact the third-party web site operators or advertisers directly if you have any questions or concerns about their data collection or privacy policies. WE HAVE NO CONTROL OR INFLUENCE OVER THE PRIVACY PRACTICES OF SUCH OTHER WEB SITES AND ADVERTISERS AND WE DO NOT ASSUME ANY RESPONSIBILITY OR LIABILITY FOR ANY THIRD-PARTY WEB SITE OPERATORS’ OR ADVERTISERS’ DATA COLLECTION PRACTICES, THEIR FAILURE TO ABIDE BY THEIR PRIVACY POLICIES OR THE CONTENT OF ANY THIRD PARTY WEB SITES.</p></div></div>
';
  Insert into BBB_CORE_PRV.BBB_Static_Template (STATIC_TEMPLATE_ID,PAGE_NAME,PAGE_TITLE,PAGE_COPY,BBB_PAGE_NAME,PAGE_TYPE,SEO_URL) values ('BBBPSI8STA10012','211','Privacy Policy',str,'PrivacyPolicy','201','/tbs/static/PrivacyPolicy');
END;
/
DECLARE
  str varchar2(32767);
BEGIN
  str := '<div id="cmsPageContent">
    <div class="grid_12 clearfix">
      <p>     
     At Bed Bath & Beyond and Buy Buy Baby, we are committed to protecting the privacy of our customers and therefore do not sell or rent our customers'' Personal Information to any third party. For the purposes of this Privacy Policy, we do not share your Personal Information with any third parties for their direct marketing purposes without first providing you the opportunity to opt-in or opt-out of such sharing. You may request information about how we manage your Personal Information consistent with this Privacy Policy by contacting us at:<a href="mailto:privacy.information@bedbath.ca"> privacy.information@bedbath.ca</a> Attn: Privacy Officer.</p>
<p>The www.bedbathandbeyond.ca web site (the "<strong>Site</strong>") is made available by Bed Bath & Beyond Inc., the parent company to Bed Bath & Beyond Canada L.P., the entity that operates retail store chains in Canada under the names Bed Bath & Beyond and Buy Buy Baby.  We refer to Bed Bath & Beyond Inc. and its subsidiaries, including Buy Buy Baby, collectively as "Bed Bath & Beyond", "we", "us" or "our".</p>
<p>This page sets forth our policies regarding the collection, use and disclosure of Personal Information we collect from or are able to obtain about the Site''s users. We refer to these policies as our "<strong>Privacy Policy</strong>." In addition to the lower case words "we" "us" and "our" defined above, you will notice that we capitalize certain words in this Privacy Policy even though the rules of grammar do not require that they be capitalized. We do this because these words have specific meanings in this Privacy Policy. These specific meanings are found where the capitalized words are placed in bold text and inside quotation marks.</p>
<p>We hope this Privacy Policy will answer your questions regarding how we treat Personal Information gathered from our Site. If you have questions or concerns regarding this Privacy Policy, other than requests related to our managing of your Personal Information, please feel free to contact us at <a href="mailto:customer.service@bedbath.ca"> customer.service@bedbath.ca</a>; for more information about your choices regarding your Personal Information, please refer to the section titled "How to Unsubscribe," below. Otherwise please direct all inquiries regarding this Privacy Policy to <a href="mailto:privacy.information@bedbath.ca">privacy.information@bedbath.ca</a> Attn: Privacy Officer.</p>
<p>This Privacy Policy does not apply to Personal Information that you may provide to us or that we may obtain through means other than the Site, such as by phone or postal mail, but please note that Personal Information obtained through such means will be treated in accordance with the first paragraph above and our use of such Personal Information will be consistent with this Privacy Policy. By accessing and using the Site you are agreeing to the terms of this Privacy Policy. If you do not agree to the terms of this Privacy Policy, you are not authorized to use the Site. Furthermore, by providing us with Personal Information on this Site, you fully understand and unambiguously consent to the collection, use, retention and disclosure of such Personal Information in Canada and/or the United States in accordance with this Privacy Policy.</p>
<p>You may withdraw your consent at any time, subject to legal or contractual restrictions and reasonable notice to us. If you withdraw your consent, we will inform you of the implications of such withdrawal. To withdraw your consent, simply contact our Privacy Officer at the contact information below and advise us of what Personal Information you no longer wish us to use. All communications with respect to any withdrawal or variation of consent should be in writing and addressed to our Privacy Officer.</p>
<p>As we update and diversify our products and services, this Privacy Policy may change. We reserve the right, at our discretion, to change, modify, add, or remove portions of this Privacy Policy at any time. Please check this page for updates periodically. If we make any material changes to this Privacy Policy, we will notify you here. Unless the law otherwise requires, we do not provide individual notice of changes to this Privacy Policy and your use of the Site following any posted changes will indicate your acceptance of such changes.  <strong>This Privacy Policy became effective on December 6, 2007 and was most recently changed on August 26, 2014.</strong> The last change to this policy was:</p>
                   <div class="bulletsReset">
      <ul>
       <li>
    Clarification regarding the trade names we host on the Site.</li>
    <li>Additional detail regarding transfer of Personal Information outside of Canada.</li>
    <li>Updated information regarding our Registries.</li>
      </ul>
     </div>
                    <h2 class="subHeading">What Information Do We Actively Collect and Inactively Gather? </h2>
                    <h3 class="subHeading">Personal Information</h3>
     <p>In order to process orders efficiently, when you place an order with us or register an account on the Site we actively collect the following personal information: name, billing address, shipping address, e-mail address, phone number, credit card information and information that may be of a personally identifiable nature, such as IP addresses, obtained through Tracking Technologies ("Personal Information"). If you prefer not to disclose your Personal Information online, you can always place your order by calling 1-800-GO BEYOND® (1-800-462-3966), or visiting one of our stores. <a href="/store/selfservice/CanadaStoreLocator">Click here</a> to locate the store nearest you and to receive driving directions.</p>
<p>We may periodically conduct surveys of visitors to our Site for internal research purposes that may include marketing. This helps us better understand what our customers would like to see from us. If you respond to such surveys, we may ask you for some Personal Information and/or may contact you for more information, but only if you have given us permission to do so.</p>
<p>We may also receive information about you from other sources.</p>
<h3 class="subHeading">Site Usage Information</h3>
<p>We (including through our third party service providers) may inactively collect from you information that is not in the same category as Personal Information. For example, we might track information about the date and time you access the Site, the type of web browser you used, and the web site from which you connect to our Site. We collect and use this and other non-Personal Information (such as demographic data) to analyze and develop our business strategy and to determine how you found out about us, your interests regarding our products and services and how to further improve our Site. We, or a third party, may also collect information from your activities on the Site as well as other sites for the purpose of providing advertisements and other content that is customized to your interests and preferences. This means that our ads may appear on participating web sites based upon your browsing activity. To accomplish all this, and also to allow us to offer our customers streamlined ordering and other useful features, we, as do many web sites, use and allow our third party advertising companies to use the technologies listed below and other similar technologies (the "<strong>Tracking Technologies</strong>"):</p>
 <div class="bulletsReset">
      <ul>
       <li><strong>Cookies and Clear GIFs</strong> - Cookies are pieces of information that are stored by your browser on your computer''s hard drive. Cookies make web surfing easier for you by saving your customer identification information (i.e., username, password, shopping cart information, etc.) while you''re at a site. Cookies also track where you travel on a site and what you look at and purchase. A clear GIF is typically a one-pixel, transparent image (although it can be a visible image as well), located on a web page or in an e-mail or other type of message, which is retrieved from a remote site on the Internet enabling the verification of an individual''s viewing or receipt of a web page or message.</li>
    <li><strong>IP Address Tracking and Clickstream Data</strong> - In addition to cookies and clear GIFs, our servers automatically collect data about your server''s Internet address when you visit our Site. This information, known as an Internet Protocol address, or "IP Address", is a number that is automatically assigned to your computer by your Internet service provider whenever you are on the Internet. When you view pages from our Site, our servers may record or "log" your IP Address and sometimes your domain name. Our server also may record the page that linked you to us and related information including any ads you may have clicked on. Such information is known as "<strong>Clickstream Data</strong>".</li>
    <li><strong>Email Interconnectivity </strong>- If you receive email from us, we may use certain tools to capture data related to when you open our message, click on any links or banners it contains and make purchases.</li></ul></div>
<p>In some cases, we may link the non-Personal information collected through the Tracking Technologies with certain of your Personal Information. WE WANT TO BE SURE THAT YOU UNDERSTAND THAT ACCEPTING TRACKING TECHNOLOGIES ONTO YOUR COMPUTER IN NO WAY GIVES US ACCESS TO YOUR COMPUTER. YOU MAY SET YOUR WEB BROWSER SOFTWARE TO REJECT TRACKING TECHNOLOGIES, BUT, IF YOU DO SO, CERTAIN FUNCTIONALITY OF THE SITE MAY BE AFFECTED. Third parties (including, but not limited to, software vendors, advertisers and fulfillment companies) may also use Tracking Technologies by or through the Site. WE HAVE NO CONTROL OVER WHAT INFORMATION SUCH THIRD PARTIES TRACK OR COLLECT, AND HAVE NO RESPONSIBILITY OR LIABILITY FOR ANY TRACKING, DATA COLLECTION OR OTHER ACTIVITIES OF SUCH THIRD PARTIES</p>
     <h3 class="subHeading">Mobile Devices</h3>
                    <p>You may be visiting the Site from your mobile device. Certain mobile service providers uniquely identify mobile devices and we or our third-party service providers may receive such information if you access the Site through mobile devices. Some features of the Site may allow for the collection of mobile phone numbers and we may associate that phone number to mobile device identification information. Furthermore, some mobile phone service providers operate systems that pinpoint the physical location of devices that use their service. Depending on the provider, we or our third-party service providers may receive this information.
</p>
<p>You understand that your wireless carrier''s standard rates may apply when you visit the Site from a mobile device. You represent that you are the owner or authorized user of the wireless device used to visit the Site, and that you are authorized to approve the applicable charges.</p>
                    <h3 class="subHeading">Security of the Information You Provide</h3>
     <p>Among our top priorities is keeping your Personal Information secure. We use Secure Sockets Layer (SSL) technology to encrypt all of your Personal Information before it is transmitted to us so that it can be appropriately safeguarded from being read or recorded as it travels over the Internet. You can tell you are sending information securely when you see our Site address change slightly, from http: to https: indicating a secure server connection is being used. The computers that store your Personal Information are located in a separate facility in the United States which employs firewall and security technology. We employ these procedures to protect your Personal Information from unauthorized access, destruction, use, modification or disclosure.</p>
<p>You may be able to create an account on our Site with a username and password, including if you set up a wish list, customer account, or a registry. To help protect your information, we have required password standards.  We recommend you exercise extreme care in protecting your password information and do not publicly share any of your passwords. You are responsible for maintaining the strict confidentiality of your account password, and you are responsible for any access to or use of the Site by you or another person or entity using your password, whether or not such access or use has been authorized by or on behalf of you.</p>
<p>Your Personal Information will be stored and processed on our computers in the United States. The laws on holding Personal Information in the United States may vary and be less stringent than laws of your province or Canada. We use commercially reasonable efforts to hold and transmit your Personal Information in a safe, confidential and secure environment in compliance with Canadian privacy legislation. If you object to your Personal Information being transferred or used in this manner please do not register with or use the Site.</p>
<p>ALTHOUGH WE TAKE (AND REQUIRE OUR THIRD-PARTY PROVIDERS TO TAKE) REASONABLE SECURITY PRECAUTIONS TO PROTECT THE PERSONAL INFORMATION COLLECTED FROM AND STORED ON THE SITE, BECAUSE OF THE OPEN NATURE OF THE INTERNET, WE CANNOT GUARANTEE THAT PERSONAL INFORMATION STORED ON OUR SERVERS, TRANSMITTED TO OR FROM A USER, OR OTHERWISE IN OUR CARE WILL BE ABSOLUTELY SAFE FROM INTRUSION BY OTHERS, SUCH AS HACKERS. ACCORDINGLY, WE DISCLAIM ANY LIABILITY FOR ANY THEFT OR LOSS OF, UNAUTHORIZED ACCESS OR DAMAGE TO, OR INTERCEPTION OF ANY DATA OR COMMUNICATIONS. BY USING THE SITE, YOU ACKNOWLEDGE THAT YOU UNDERSTAND AND AGREE TO ASSUME THESE RISKS.</p>
<h3 class="subHeading">How Do We Use the Information that You Provide and We Collect?</h3>
     <p>We collect Personal Information for the purposes of fulfilling orders, processing returns and exchanges and providing you with the services you have chosen, providing a high level of customer service, providing you with information we believe may be of interest including mailing our circulars and special offers, for editorial and feedback purposes, for internal marketing and promotional purposes, for a statistical analysis of your behavior, for product development, for content improvement, for protection against fraud and error, and to customize the content and layout of the Site (collectively, "<strong>Information Usage Purposes</strong>"). Notwithstanding the foregoing, we will not knowingly use a mobile phone number for marketing purposes without first obtaining your consent. In performing the Information Usage Purposes, we may combine information we collect on the Site with information we receive from other sources. We do not sell Personal Information and we do not disclose Personal Information to any other party, except as set forth in this Privacy Policy or as required by applicable law or regulatory requirements. Unless otherwise provided in this Privacy Policy, any non-Personal Information you transmit to the Site or we collect from the Site will be treated as non-confidential and non-proprietary and you agree that we may use any such non-Personal Information (together with any ideas, concepts, know-how, or techniques contained therein) in any manner, for any purpose, without credit or compensation to you. Without limiting the generality or application of the foregoing, the information that you publish on or through our registries is governed by specific terms available below under the heading "Registry Information".</p>
                    <h3 class="subHeading">Transfer of Your Information; Sharing of Your Information with Our Partners </h3>
     <p>We may use, store or share Personal Information we collect in the course of our operations in Canada outside of Canada.  For example, your Personal Information will be stored and processed on our computers in Canada and the United States.  We may also provide your Personal Information to our associates and to third party service providers acting as our authorized agents or partners in providing our services or co-branded services to you and in performing Information Usage Purposes. Our associates and third party service providers may be located outside of Canada.</p>
<p>We use commercially reasonable efforts to hold and transmit your Personal Information in a safe, confidential and secure environment in compliance with Canadian privacy legislation.  In addition, we screen and reasonably determine to be reputable any such third party to whom we disclose such Personal Information and we require such third parties to agree to use the Personal Information only for such specified purposes and to implement and maintain reasonable security procedures and practices appropriate to the nature of your Personal Information in order to protect your Personal Information from unauthorized access, destruction, use, modification or disclosure.
</p>
<p>If we merge, sell, restructure, or finance or otherwise transfer our assets, Site or operations, or if we are involved in some form of business combination or joint venture, we may disclose or transfer your Personal Information in connection with such transaction provided that, where appropriate, any party to whom the information is disclosed is bound by agreements or obligations, and required to use or disclose your personal information in a manner consistent with the use and disclosure provisions of this Privacy Policy, unless you consent otherwise.</p>
<h3 class="subHeading">Registry Information</h3>
<p>Wedding and Baby Registry information is treated differently from information submitted for other purposes on our Site. Registry users may receive additional e-mail communications from us, such as a registry welcome e-mail and follow-up contact after the scheduled event date or the baby''s expected arrival date. In addition, cookies are used as part of our registry service to save your registry number on your computer. By registering with us and opting to make your registry available online, your registry information, which includes your name, the province where you live, your event, event date or baby''s expected arrival date, and type of event will be available publicly and may be used by our partners that help provide our services and our co-branded services (including for Information Usage Purposes) and will be accessible by search engines and spiders that are not affiliated with us.</p>
<p>Finally, and to illustrate the type of business partner with whom your registry information may be shared, if you have taken advantage of the applicable opt-in procedure, your registry information (your name, province where you live, date of event, baby''s expected arrival date and event location) may be made available to third party wedding and baby registry portal sites. We may also share the e-mail address associated with your registry, but solely to enable us and such a third-party registry portal site to link your purchases or other activity with the registry you have established with us. The use of such portal sites is intended to provide your guests with additional means of finding your registry, and to provide you with additional ways to create a registry with us. Any third-party registry portal site with whom an e-mail address is shared as a registry identifier is contractually prohibited from using it for any other purpose not outlined above. In the future, if you wish to unsubscribe from our sharing of your information with such sites, please send an e-mail to <a href="mailto:customer.service@bedbath.ca">customer.service@bedbath.ca</a> clearly indicating such request.</p>
<h3 class="subHeading">Use of Information in Sweepstakes and Contests</h3>
<p>Occasionally, we may run a promotion such as a sweepstakes or a contest (a "Promotion"), and you will be given the opportunity to enter by filling out an entry form, or in some cases, you may be automatically entered by using a designated type of credit card when placing an order in store, online or via phone or, for example, by creating a Wedding or Baby Registry. Your entry information may be shared with third parties for the limited purposes of administering the sweepstakes and selecting a winner(s) and may be used to notify you in connection with the sweepstakes or contest and to verify your identity. When you enter such a Promotion, you will have the opportunity to consent to receive future communications from us. Since we value your privacy, you will always have the opportunity to stop such communications as detailed below. In addition, some Promotions may have one or more co-sponsors, and you may also be asked upon entry to consent to receive future communications from such co-sponsors. In the event your consent is indicated, the information from your entry will be shared with the co-sponsors. In those situations, the third parties will have the right to use your information for their own purposes, in accordance with their own policies, and we are not responsible for how co-sponsors may use your information. In all events, we will make it clear to you in the entry, order or registration form, before you submit it, whether you are consenting to receive information from any party, and you will be given the opportunity to confirm or withdraw such consent prior to submitting your entry, order or registration.</p>
<p>In any case, where you elect to have us share your information with third parties, such as in the Promotions discussed above, those elections to have your information shared will supersede anything to the contrary in this Privacy Policy.
</p>
<h3 class="subHeading">How to Unsubscribe</h3>
<p>We will only send e-mail for customer service purposes and to those visitors who have indicated that they wish to receive other types of e-mail from us and our subsidiaries.</p>
<p>If at any time you wish to stop receiving e-mail from us, we include an unsubscribe mechanism in all of our e-mails and you can always remove your name from our mailing list by sending an e-mail to <a href="mailto:customer.service@bedbath.ca">customer.service@bedbath.ca</a> stating that you wish to unsubscribe and indicating in the subject line of the e-mail "NO E-MAIL". Although we will promptly remove your name from our e-mail list upon receiving your request, it is possible that you may still receive e-mails from us that had been initiated prior to your name being removed from our list.</p>
<p>THE UNSUBSCRIBE RIGHTS AND PROCEDURES DESCRIBED IN THIS SECTION ARE THE DEFAULT RULES AND PROCEDURES WE USE IN OPERATING THE SITE AND ARE THE STANDARD BY WHICH WE ADHERE TO OUR PRIVACY POLICY.</p>
<p>Please keep in mind that, if you choose not to receive marketing communications, you will continue to receive transactional or account communications related to services or products that you have requested on the Site (e.g. confirmation e-mails, billing reminders).</p>
<h3 class="subHeading">Release of Information to Protect Our Site and Others</h3>
<p>We may release Personal Information as permitted or required by applicable law or regulatory requirements, to comply with valid legal processes such as search warrants, subpoenas or court orders, to seek advice from our legal counsel, as part of our regular reporting activity to related companies, to protect the rights, property or safety of Bed Bath & Beyond, Buy Buy BABY, our users or others, to address emergency situations or where necessary to protect the safety of a person or group, to enforce or apply the terms of any of our user agreements, or where the Personal Information is publicly available.</p>
<h3 class="subHeading">Your Account Access Rights</h3>
<p>You may review and make changes to the Personal Information that is stored in your user account on the Site by visiting the "My Account" area of the Site.</p>
<p>You can request access to your Personal Information stored by Bed Bath & Beyond by contacting our Privacy Officer (contact information below) and we will inform you about what type of Personal Information we have on record or in our control, how it is used and to whom it may have been disclosed, provide you with access to your information so you can review and verify the accuracy and completeness and request changes to the information and make any necessary updates to your Personal Information.</p>
<p>There are instances where applicable law or regulatory requirements allow or require us to refuse to provide some or all of the Personal Information that we hold about you. In addition, the Personal Information may have been destroyed, erased or made anonymous in accordance with our record retention obligations and practices. If we do not provide you with access to your Personal Information, we will endeavor to inform you of the reasons why, subject to any legal or regulatory restrictions.</p>
</p>
<h3 class="subHeading">Intended Users; Use by Children under 13</h3>
<p>The Site is not purposefully directed at children under the age of 13 and all products available for sale on the Site are intended to be purchased by adults. We do not collect or maintain Personal Information, via the Site or otherwise, from those we actually know are children under 13 years of age. If you notify us that information has been provided by a child under the age of 13, upon verification, we will promptly delete such information.</p>
<h3 class="subHeading">Third Party Ad Servers; Links to Other Web Sites</h3>
<p>We may use third-party advertising companies to serve ads when you visit our Site. The Site also may contain links to other web sites that are not under our control. These links may include the use of so-called "share links." A "share link" is a button and/or text link appearing on our Site that enables the launch of a sharing mechanism whereby you can post links to, and content from, the Site onto social networking web sites such as Facebook. The privacy policies of other web site operators or advertisers may significantly differ from our Privacy Policy. We urge you to read the privacy statements of each and every web site you visit and to contact the third-party Web site operators or advertisers directly if you have any questions or concerns about their data collection or privacy policies. WE HAVE NO CONTROL OR INFLUENCE OVER THE PRIVACY PRACTICES OF SUCH OTHER WEB SITES AND ADVERTISERS AND WE DO NOT ASSUME ANY RESPONSIBILITY OR LIABILITY FOR ANY THIRD-PARTY WEB SITE OPERATORS'' OR ADVERTISERS'' DATA COLLECTION PRACTICES, THEIR FAILURE TO ABIDE BY THEIR PRIVACY POLICIES OR THE CONTENT OF ANY THIRD PARTY WEB SITES.</p>
<h3 class="subHeading">Inquiries or Concerns</h3>
<p>If you have any questions about this Privacy Policy or concerns about how we manage your Personal Information, including how your Personal Information is collected, stored or processed by third party service providers outside of Canada, please contact our Privacy Officer. If you are dissatisfied with our response, you may be entitled to make a written submission to the federal Privacy Commissioner.</p>
<h3 class="subHeading">Privacy Officer</h3>
<p>We have appointed a Privacy Officer to oversee compliance with this Privacy Policy. The contact information for our Privacy Officer is as follows:</p> 
<ul><li>Privacy Officer</li>
<li>Bed Bath & Beyond Canada L.P.,</li>
<li>845 Marine Drive</li>
<li>Unit 200 North Vancouver B.C. Canada, V7P 1R7</li>
<li>T. 1-877-222-5939</li>
<li>E. <a href="mailto:privacy.information@bedbath.ca"> privacy.information@bedbath.ca</a></li></ul>
<h3 class="subHeading">Interpretation of this Privacy Policy</h3>
<p>Any interpretation associated with this Privacy Policy will be made by our Privacy Officer. This Privacy Policy does not create or confer upon any individual any rights, or impose upon Bed Bath & Beyond any rights or obligations in addition to any rights or obligations imposed by Canada''s applicable federal and provincial privacy laws. Should there be any inconsistency between this Privacy Policy and the applicable federal and provincial privacy laws, this Privacy Policy will be interpreted so as to give effect to, and comply with, those privacy laws which are applicable.</p>
    </div></div>';
  Insert into BBB_CORE_PRV.BBB_Static_Template (STATIC_TEMPLATE_ID,PAGE_NAME,PAGE_TITLE,PAGE_COPY,BBB_PAGE_NAME,PAGE_TYPE,SEO_URL) values ('BBBPSI8STA10013','211','Privacy Policy',str,'PrivacyPolicy','201','/tbs/static/PrivacyPolicy');
END;
/

--- insert site static page entry : BBB_CORE_PRV.BBB_Static_Site

Insert into BBB_CORE_PRV.BBB_Static_Site (STATIC_TEMPLATE_ID,SITE_ID) values ('BBBPSI8STA10012','TBS_BuyBuyBaby');
Insert into BBB_CORE_PRV.BBB_Static_Site (STATIC_TEMPLATE_ID,SITE_ID) values ('BBBPSI8STA10013','TBS_BedBathCanada');

--- insert static data: BBB_CORE_PRV.BBB_Static_Template

DECLARE
  str varchar2(32767);
BEGIN
  str := '<div id="cmsPageContent">
    <div class="grid_12 clearfix">
      <p>     
     At Bed Bath & Beyond and Buy Buy Baby, we are committed to protecting the privacy of our customers and therefore do not sell or rent our customers'' Personal Information to any third party. For the purposes of this Privacy Policy, we do not share your Personal Information with any third parties for their direct marketing purposes without first providing you the opportunity to opt-in or opt-out of such sharing. You may request information about how we manage your Personal Information consistent with this Privacy Policy by contacting us at:<a href="mailto:privacy.information@bedbath.ca"> privacy.information@bedbath.ca</a> Attn: Privacy Officer.</p>
<p>The www.bedbathandbeyond.ca web site (the "<strong>Site</strong>") is made available by Bed Bath & Beyond Inc., the parent company to Bed Bath & Beyond Canada L.P., the entity that operates retail store chains in Canada under the names Bed Bath & Beyond and Buy Buy Baby.  We refer to Bed Bath & Beyond Inc. and its subsidiaries, including Buy Buy Baby, collectively as "Bed Bath & Beyond", "we", "us" or "our".</p>
<p>This page sets forth our policies regarding the collection, use and disclosure of Personal Information we collect from or are able to obtain about the Site''s users. We refer to these policies as our "<strong>Privacy Policy</strong>." In addition to the lower case words "we" "us" and "our" defined above, you will notice that we capitalize certain words in this Privacy Policy even though the rules of grammar do not require that they be capitalized. We do this because these words have specific meanings in this Privacy Policy. These specific meanings are found where the capitalized words are placed in bold text and inside quotation marks.</p>
<p>We hope this Privacy Policy will answer your questions regarding how we treat Personal Information gathered from our Site. If you have questions or concerns regarding this Privacy Policy, other than requests related to our managing of your Personal Information, please feel free to contact us at <a href="mailto:customer.service@bedbath.ca"> customer.service@bedbath.ca</a>; for more information about your choices regarding your Personal Information, please refer to the section titled "How to Unsubscribe," below. Otherwise please direct all inquiries regarding this Privacy Policy to <a href="mailto:privacy.information@bedbath.ca">privacy.information@bedbath.ca</a> Attn: Privacy Officer.</p>
<p>This Privacy Policy does not apply to Personal Information that you may provide to us or that we may obtain through means other than the Site, such as by phone or postal mail, but please note that Personal Information obtained through such means will be treated in accordance with the first paragraph above and our use of such Personal Information will be consistent with this Privacy Policy. By accessing and using the Site you are agreeing to the terms of this Privacy Policy. If you do not agree to the terms of this Privacy Policy, you are not authorized to use the Site. Furthermore, by providing us with Personal Information on this Site, you fully understand and unambiguously consent to the collection, use, retention and disclosure of such Personal Information in Canada and/or the United States in accordance with this Privacy Policy.</p>
<p>You may withdraw your consent at any time, subject to legal or contractual restrictions and reasonable notice to us. If you withdraw your consent, we will inform you of the implications of such withdrawal. To withdraw your consent, simply contact our Privacy Officer at the contact information below and advise us of what Personal Information you no longer wish us to use. All communications with respect to any withdrawal or variation of consent should be in writing and addressed to our Privacy Officer.</p>
<p>As we update and diversify our products and services, this Privacy Policy may change. We reserve the right, at our discretion, to change, modify, add, or remove portions of this Privacy Policy at any time. Please check this page for updates periodically. If we make any material changes to this Privacy Policy, we will notify you here. Unless the law otherwise requires, we do not provide individual notice of changes to this Privacy Policy and your use of the Site following any posted changes will indicate your acceptance of such changes.  <strong>This Privacy Policy became effective on December 6, 2007 and was most recently changed on August 26, 2014.</strong> The last change to this policy was:</p>
                   <div class="bulletsReset">
      <ul>
       <li>
    Clarification regarding the trade names we host on the Site.</li>
    <li>Additional detail regarding transfer of Personal Information outside of Canada.</li>
    <li>Updated information regarding our Registries.</li>
      </ul>
     </div>
                    <h2 class="subHeading">What Information Do We Actively Collect and Inactively Gather? </h2>
                    <h3 class="subHeading">Personal Information</h3>
     <p>In order to process orders efficiently, when you place an order with us or register an account on the Site we actively collect the following personal information: name, billing address, shipping address, e-mail address, phone number, credit card information and information that may be of a personally identifiable nature, such as IP addresses, obtained through Tracking Technologies ("Personal Information"). If you prefer not to disclose your Personal Information online, you can always place your order by calling 1-800-GO BEYOND® (1-800-462-3966), or visiting one of our stores. <a href="/store/selfservice/CanadaStoreLocator">Click here</a> to locate the store nearest you and to receive driving directions.</p>
<p>We may periodically conduct surveys of visitors to our Site for internal research purposes that may include marketing. This helps us better understand what our customers would like to see from us. If you respond to such surveys, we may ask you for some Personal Information and/or may contact you for more information, but only if you have given us permission to do so.</p>
<p>We may also receive information about you from other sources.</p>
<h3 class="subHeading">Site Usage Information</h3>
<p>We (including through our third party service providers) may inactively collect from you information that is not in the same category as Personal Information. For example, we might track information about the date and time you access the Site, the type of web browser you used, and the web site from which you connect to our Site. We collect and use this and other non-Personal Information (such as demographic data) to analyze and develop our business strategy and to determine how you found out about us, your interests regarding our products and services and how to further improve our Site. We, or a third party, may also collect information from your activities on the Site as well as other sites for the purpose of providing advertisements and other content that is customized to your interests and preferences. This means that our ads may appear on participating web sites based upon your browsing activity. To accomplish all this, and also to allow us to offer our customers streamlined ordering and other useful features, we, as do many web sites, use and allow our third party advertising companies to use the technologies listed below and other similar technologies (the "<strong>Tracking Technologies</strong>"):</p>
 <div class="bulletsReset">
      <ul>
       <li><strong>Cookies and Clear GIFs</strong> - Cookies are pieces of information that are stored by your browser on your computer''s hard drive. Cookies make web surfing easier for you by saving your customer identification information (i.e., username, password, shopping cart information, etc.) while you''re at a site. Cookies also track where you travel on a site and what you look at and purchase. A clear GIF is typically a one-pixel, transparent image (although it can be a visible image as well), located on a web page or in an e-mail or other type of message, which is retrieved from a remote site on the Internet enabling the verification of an individual''s viewing or receipt of a web page or message.</li>
    <li><strong>IP Address Tracking and Clickstream Data</strong> - In addition to cookies and clear GIFs, our servers automatically collect data about your server''s Internet address when you visit our Site. This information, known as an Internet Protocol address, or "IP Address", is a number that is automatically assigned to your computer by your Internet service provider whenever you are on the Internet. When you view pages from our Site, our servers may record or "log" your IP Address and sometimes your domain name. Our server also may record the page that linked you to us and related information including any ads you may have clicked on. Such information is known as "<strong>Clickstream Data</strong>".</li>
    <li><strong>Email Interconnectivity </strong>- If you receive email from us, we may use certain tools to capture data related to when you open our message, click on any links or banners it contains and make purchases.</li></ul></div>
<p>In some cases, we may link the non-Personal information collected through the Tracking Technologies with certain of your Personal Information. WE WANT TO BE SURE THAT YOU UNDERSTAND THAT ACCEPTING TRACKING TECHNOLOGIES ONTO YOUR COMPUTER IN NO WAY GIVES US ACCESS TO YOUR COMPUTER. YOU MAY SET YOUR WEB BROWSER SOFTWARE TO REJECT TRACKING TECHNOLOGIES, BUT, IF YOU DO SO, CERTAIN FUNCTIONALITY OF THE SITE MAY BE AFFECTED. Third parties (including, but not limited to, software vendors, advertisers and fulfillment companies) may also use Tracking Technologies by or through the Site. WE HAVE NO CONTROL OVER WHAT INFORMATION SUCH THIRD PARTIES TRACK OR COLLECT, AND HAVE NO RESPONSIBILITY OR LIABILITY FOR ANY TRACKING, DATA COLLECTION OR OTHER ACTIVITIES OF SUCH THIRD PARTIES</p>
     <h3 class="subHeading">Mobile Devices</h3>
                    <p>You may be visiting the Site from your mobile device. Certain mobile service providers uniquely identify mobile devices and we or our third-party service providers may receive such information if you access the Site through mobile devices. Some features of the Site may allow for the collection of mobile phone numbers and we may associate that phone number to mobile device identification information. Furthermore, some mobile phone service providers operate systems that pinpoint the physical location of devices that use their service. Depending on the provider, we or our third-party service providers may receive this information.
</p>
<p>You understand that your wireless carrier''s standard rates may apply when you visit the Site from a mobile device. You represent that you are the owner or authorized user of the wireless device used to visit the Site, and that you are authorized to approve the applicable charges.</p>
                    <h3 class="subHeading">Security of the Information You Provide</h3>
     <p>Among our top priorities is keeping your Personal Information secure. We use Secure Sockets Layer (SSL) technology to encrypt all of your Personal Information before it is transmitted to us so that it can be appropriately safeguarded from being read or recorded as it travels over the Internet. You can tell you are sending information securely when you see our Site address change slightly, from http: to https: indicating a secure server connection is being used. The computers that store your Personal Information are located in a separate facility in the United States which employs firewall and security technology. We employ these procedures to protect your Personal Information from unauthorized access, destruction, use, modification or disclosure.</p>
<p>You may be able to create an account on our Site with a username and password, including if you set up a wish list, customer account, or a registry. To help protect your information, we have required password standards.  We recommend you exercise extreme care in protecting your password information and do not publicly share any of your passwords. You are responsible for maintaining the strict confidentiality of your account password, and you are responsible for any access to or use of the Site by you or another person or entity using your password, whether or not such access or use has been authorized by or on behalf of you.</p>
<p>Your Personal Information will be stored and processed on our computers in the United States. The laws on holding Personal Information in the United States may vary and be less stringent than laws of your province or Canada. We use commercially reasonable efforts to hold and transmit your Personal Information in a safe, confidential and secure environment in compliance with Canadian privacy legislation. If you object to your Personal Information being transferred or used in this manner please do not register with or use the Site.</p>
<p>ALTHOUGH WE TAKE (AND REQUIRE OUR THIRD-PARTY PROVIDERS TO TAKE) REASONABLE SECURITY PRECAUTIONS TO PROTECT THE PERSONAL INFORMATION COLLECTED FROM AND STORED ON THE SITE, BECAUSE OF THE OPEN NATURE OF THE INTERNET, WE CANNOT GUARANTEE THAT PERSONAL INFORMATION STORED ON OUR SERVERS, TRANSMITTED TO OR FROM A USER, OR OTHERWISE IN OUR CARE WILL BE ABSOLUTELY SAFE FROM INTRUSION BY OTHERS, SUCH AS HACKERS. ACCORDINGLY, WE DISCLAIM ANY LIABILITY FOR ANY THEFT OR LOSS OF, UNAUTHORIZED ACCESS OR DAMAGE TO, OR INTERCEPTION OF ANY DATA OR COMMUNICATIONS. BY USING THE SITE, YOU ACKNOWLEDGE THAT YOU UNDERSTAND AND AGREE TO ASSUME THESE RISKS.</p>
<h3 class="subHeading">How Do We Use the Information that You Provide and We Collect?</h3>
     <p>We collect Personal Information for the purposes of fulfilling orders, processing returns and exchanges and providing you with the services you have chosen, providing a high level of customer service, providing you with information we believe may be of interest including mailing our circulars and special offers, for editorial and feedback purposes, for internal marketing and promotional purposes, for a statistical analysis of your behavior, for product development, for content improvement, for protection against fraud and error, and to customize the content and layout of the Site (collectively, "<strong>Information Usage Purposes</strong>"). Notwithstanding the foregoing, we will not knowingly use a mobile phone number for marketing purposes without first obtaining your consent. In performing the Information Usage Purposes, we may combine information we collect on the Site with information we receive from other sources. We do not sell Personal Information and we do not disclose Personal Information to any other party, except as set forth in this Privacy Policy or as required by applicable law or regulatory requirements. Unless otherwise provided in this Privacy Policy, any non-Personal Information you transmit to the Site or we collect from the Site will be treated as non-confidential and non-proprietary and you agree that we may use any such non-Personal Information (together with any ideas, concepts, know-how, or techniques contained therein) in any manner, for any purpose, without credit or compensation to you. Without limiting the generality or application of the foregoing, the information that you publish on or through our registries is governed by specific terms available below under the heading "Registry Information".</p>
                    <h3 class="subHeading">Transfer of Your Information; Sharing of Your Information with Our Partners </h3>
     <p>We may use, store or share Personal Information we collect in the course of our operations in Canada outside of Canada.  For example, your Personal Information will be stored and processed on our computers in Canada and the United States.  We may also provide your Personal Information to our associates and to third party service providers acting as our authorized agents or partners in providing our services or co-branded services to you and in performing Information Usage Purposes. Our associates and third party service providers may be located outside of Canada.</p>
<p>We use commercially reasonable efforts to hold and transmit your Personal Information in a safe, confidential and secure environment in compliance with Canadian privacy legislation.  In addition, we screen and reasonably determine to be reputable any such third party to whom we disclose such Personal Information and we require such third parties to agree to use the Personal Information only for such specified purposes and to implement and maintain reasonable security procedures and practices appropriate to the nature of your Personal Information in order to protect your Personal Information from unauthorized access, destruction, use, modification or disclosure.
</p>
<p>If we merge, sell, restructure, or finance or otherwise transfer our assets, Site or operations, or if we are involved in some form of business combination or joint venture, we may disclose or transfer your Personal Information in connection with such transaction provided that, where appropriate, any party to whom the information is disclosed is bound by agreements or obligations, and required to use or disclose your personal information in a manner consistent with the use and disclosure provisions of this Privacy Policy, unless you consent otherwise.</p>
<h3 class="subHeading">Registry Information</h3>
<p>Wedding and Baby Registry information is treated differently from information submitted for other purposes on our Site. Registry users may receive additional e-mail communications from us, such as a registry welcome e-mail and follow-up contact after the scheduled event date or the baby''s expected arrival date. In addition, cookies are used as part of our registry service to save your registry number on your computer. By registering with us and opting to make your registry available online, your registry information, which includes your name, the province where you live, your event, event date or baby''s expected arrival date, and type of event will be available publicly and may be used by our partners that help provide our services and our co-branded services (including for Information Usage Purposes) and will be accessible by search engines and spiders that are not affiliated with us.</p>
<p>Finally, and to illustrate the type of business partner with whom your registry information may be shared, if you have taken advantage of the applicable opt-in procedure, your registry information (your name, province where you live, date of event, baby''s expected arrival date and event location) may be made available to third party wedding and baby registry portal sites. We may also share the e-mail address associated with your registry, but solely to enable us and such a third-party registry portal site to link your purchases or other activity with the registry you have established with us. The use of such portal sites is intended to provide your guests with additional means of finding your registry, and to provide you with additional ways to create a registry with us. Any third-party registry portal site with whom an e-mail address is shared as a registry identifier is contractually prohibited from using it for any other purpose not outlined above. In the future, if you wish to unsubscribe from our sharing of your information with such sites, please send an e-mail to <a href="mailto:customer.service@bedbath.ca">customer.service@bedbath.ca</a> clearly indicating such request.</p>
<h3 class="subHeading">Use of Information in Sweepstakes and Contests</h3>
<p>Occasionally, we may run a promotion such as a sweepstakes or a contest (a "Promotion"), and you will be given the opportunity to enter by filling out an entry form, or in some cases, you may be automatically entered by using a designated type of credit card when placing an order in store, online or via phone or, for example, by creating a Wedding or Baby Registry. Your entry information may be shared with third parties for the limited purposes of administering the sweepstakes and selecting a winner(s) and may be used to notify you in connection with the sweepstakes or contest and to verify your identity. When you enter such a Promotion, you will have the opportunity to consent to receive future communications from us. Since we value your privacy, you will always have the opportunity to stop such communications as detailed below. In addition, some Promotions may have one or more co-sponsors, and you may also be asked upon entry to consent to receive future communications from such co-sponsors. In the event your consent is indicated, the information from your entry will be shared with the co-sponsors. In those situations, the third parties will have the right to use your information for their own purposes, in accordance with their own policies, and we are not responsible for how co-sponsors may use your information. In all events, we will make it clear to you in the entry, order or registration form, before you submit it, whether you are consenting to receive information from any party, and you will be given the opportunity to confirm or withdraw such consent prior to submitting your entry, order or registration.</p>
<p>In any case, where you elect to have us share your information with third parties, such as in the Promotions discussed above, those elections to have your information shared will supersede anything to the contrary in this Privacy Policy.
</p>
<h3 class="subHeading">How to Unsubscribe</h3>
<p>We will only send e-mail for customer service purposes and to those visitors who have indicated that they wish to receive other types of e-mail from us and our subsidiaries.</p>
<p>If at any time you wish to stop receiving e-mail from us, we include an unsubscribe mechanism in all of our e-mails and you can always remove your name from our mailing list by sending an e-mail to <a href="mailto:customer.service@bedbath.ca">customer.service@bedbath.ca</a> stating that you wish to unsubscribe and indicating in the subject line of the e-mail "NO E-MAIL". Although we will promptly remove your name from our e-mail list upon receiving your request, it is possible that you may still receive e-mails from us that had been initiated prior to your name being removed from our list.</p>
<p>THE UNSUBSCRIBE RIGHTS AND PROCEDURES DESCRIBED IN THIS SECTION ARE THE DEFAULT RULES AND PROCEDURES WE USE IN OPERATING THE SITE AND ARE THE STANDARD BY WHICH WE ADHERE TO OUR PRIVACY POLICY.</p>
<p>Please keep in mind that, if you choose not to receive marketing communications, you will continue to receive transactional or account communications related to services or products that you have requested on the Site (e.g. confirmation e-mails, billing reminders).</p>
<h3 class="subHeading">Release of Information to Protect Our Site and Others</h3>
<p>We may release Personal Information as permitted or required by applicable law or regulatory requirements, to comply with valid legal processes such as search warrants, subpoenas or court orders, to seek advice from our legal counsel, as part of our regular reporting activity to related companies, to protect the rights, property or safety of Bed Bath & Beyond, Buy Buy BABY, our users or others, to address emergency situations or where necessary to protect the safety of a person or group, to enforce or apply the terms of any of our user agreements, or where the Personal Information is publicly available.</p>
<h3 class="subHeading">Your Account Access Rights</h3>
<p>You may review and make changes to the Personal Information that is stored in your user account on the Site by visiting the "My Account" area of the Site.</p>
<p>You can request access to your Personal Information stored by Bed Bath & Beyond by contacting our Privacy Officer (contact information below) and we will inform you about what type of Personal Information we have on record or in our control, how it is used and to whom it may have been disclosed, provide you with access to your information so you can review and verify the accuracy and completeness and request changes to the information and make any necessary updates to your Personal Information.</p>
<p>There are instances where applicable law or regulatory requirements allow or require us to refuse to provide some or all of the Personal Information that we hold about you. In addition, the Personal Information may have been destroyed, erased or made anonymous in accordance with our record retention obligations and practices. If we do not provide you with access to your Personal Information, we will endeavor to inform you of the reasons why, subject to any legal or regulatory restrictions.</p>
</p>
<h3 class="subHeading">Intended Users; Use by Children under 13</h3>
<p>The Site is not purposefully directed at children under the age of 13 and all products available for sale on the Site are intended to be purchased by adults. We do not collect or maintain Personal Information, via the Site or otherwise, from those we actually know are children under 13 years of age. If you notify us that information has been provided by a child under the age of 13, upon verification, we will promptly delete such information.</p>
<h3 class="subHeading">Third Party Ad Servers; Links to Other Web Sites</h3>
<p>We may use third-party advertising companies to serve ads when you visit our Site. The Site also may contain links to other web sites that are not under our control. These links may include the use of so-called "share links." A "share link" is a button and/or text link appearing on our Site that enables the launch of a sharing mechanism whereby you can post links to, and content from, the Site onto social networking web sites such as Facebook. The privacy policies of other web site operators or advertisers may significantly differ from our Privacy Policy. We urge you to read the privacy statements of each and every web site you visit and to contact the third-party Web site operators or advertisers directly if you have any questions or concerns about their data collection or privacy policies. WE HAVE NO CONTROL OR INFLUENCE OVER THE PRIVACY PRACTICES OF SUCH OTHER WEB SITES AND ADVERTISERS AND WE DO NOT ASSUME ANY RESPONSIBILITY OR LIABILITY FOR ANY THIRD-PARTY WEB SITE OPERATORS'' OR ADVERTISERS'' DATA COLLECTION PRACTICES, THEIR FAILURE TO ABIDE BY THEIR PRIVACY POLICIES OR THE CONTENT OF ANY THIRD PARTY WEB SITES.</p>
<h3 class="subHeading">Inquiries or Concerns</h3>
<p>If you have any questions about this Privacy Policy or concerns about how we manage your Personal Information, including how your Personal Information is collected, stored or processed by third party service providers outside of Canada, please contact our Privacy Officer. If you are dissatisfied with our response, you may be entitled to make a written submission to the federal Privacy Commissioner.</p>
<h3 class="subHeading">Privacy Officer</h3>
<p>We have appointed a Privacy Officer to oversee compliance with this Privacy Policy. The contact information for our Privacy Officer is as follows:</p> 
<ul><li>Privacy Officer</li>
<li>Bed Bath & Beyond Canada L.P.,</li>
<li>845 Marine Drive</li>
<li>Unit 200 North Vancouver B.C. Canada, V7P 1R7</li>
<li>T. 1-877-222-5939</li>
<li>E. <a href="mailto:privacy.information@bedbath.ca"> privacy.information@bedbath.ca</a></li></ul>
<h3 class="subHeading">Interpretation of this Privacy Policy</h3>
<p>Any interpretation associated with this Privacy Policy will be made by our Privacy Officer. This Privacy Policy does not create or confer upon any individual any rights, or impose upon Bed Bath & Beyond any rights or obligations in addition to any rights or obligations imposed by Canada''s applicable federal and provincial privacy laws. Should there be any inconsistency between this Privacy Policy and the applicable federal and provincial privacy laws, this Privacy Policy will be interpreted so as to give effect to, and comply with, those privacy laws which are applicable.</p>
    </div></div>';
  Insert into BBB_CORE_PRV.BBB_Static_Template (STATIC_TEMPLATE_ID,PAGE_NAME,PAGE_TITLE,PAGE_COPY,BBB_PAGE_NAME,PAGE_TYPE,SEO_URL) values ('BBBPSI8STA10009','211','Privacy Policy',str,'PrivacyPolicy','201','/tbs/static/PrivacyPolicy');
END;
/
Insert into BBB_CORE_PRV.BBB_Static_Template (STATIC_TEMPLATE_ID,PAGE_NAME,PAGE_TITLE,PAGE_COPY,BBB_PAGE_NAME,PAGE_TYPE,SEO_URL) values ('StaticTmpVdc_tbs_us','218','Item Ships Directly from Vendor','
    <div class="grid_8 cb"> 
     <div class="bulletsReset">
      <ul>
       <li>Item ships directly from vendor. Please see the cart for specific shipping time frames.</li></strong>
       <li>This item can not be gift packaged</li>
       <li>Express and Expedited shipping options are not available on this item. If you have other items in your order you wish to have shipped via Express or Expedited shipping, please create a separate order for those items or select the Multiple Addresses option during checkout.</li>
      </ul>
     </div>
    </div>
','ItemShippedDirectlyFromVendor','202','/tbs/static/ItemShippedDirectlyFromVendor');
Insert into BBB_CORE_PRV.BBB_Static_Site (STATIC_TEMPLATE_ID,SITE_ID) values ('BBBPSI8STA10009','TBS_BedBathUS');
Insert into BBB_CORE_PRV.BBB_Static_Site (STATIC_TEMPLATE_ID,SITE_ID) values ('StaticTmpVdc_tbs_us','TBS_BedBathUS');

--- insert static2 data : BBB_CORE_PRV.BBB_Static_Template

Insert into BBB_CORE_PRV.BBB_Static_Template (STATIC_TEMPLATE_ID,PAGE_NAME,PAGE_TITLE,PAGE_COPY,BBB_PAGE_NAME,PAGE_TYPE,SEO_URL) values ('StaticTmpVdc_tbs_canada','218','Item Ships Directly from Vendor','
    <div class="grid_8 cb"> 
     <div class="bulletsReset">
      <ul>
       <li>Item ships directly from vendor. Please see the cart for specific shipping time frames.</li></strong>
       <li>This item can not be gift packaged</li>
       <li>Express and Expedited shipping options are not available on this item. If you have other items in your order you wish to have shipped via Express or Expedited shipping, please create a separate order for those items or select the Multiple Addresses option during checkout.</li>
      </ul>
     </div>
    </div>
','ItemShippedDirectlyFromVendor','202','/tbs/static/ItemShippedDirectlyFromVendor');
Insert into BBB_CORE_PRV.BBB_Static_Template (STATIC_TEMPLATE_ID,PAGE_NAME,PAGE_TITLE,PAGE_COPY,BBB_PAGE_NAME,PAGE_TYPE,SEO_URL) values ('StaticTmpVdc_tbs_baby','218','Item Ships Directly from Vendor','
    <div class="grid_8 cb"> 
     <div class="bulletsReset">
      <ul>
       <li>Item ships directly from vendor</li>
       <li>This item can not be gift packaged</li>
       <li>Express and Expedited shipping options are not available on this item. If you have other items in your order you wish to have shipped via Express or Expedited shipping, please create a separate order for those items or select the Multiple Addresses option during checkout.</li>
      </ul>
     </div>
    </div>
','ItemShippedDirectlyFromVendor','202','/tbs/static/ItemShippedDirectlyFromVendor');
Insert into BBB_CORE_PRV.BBB_Static_Site (STATIC_TEMPLATE_ID,SITE_ID) values ('StaticTmpVdc_tbs_canada','TBS_BedBathCanada');
Insert into BBB_CORE_PRV.BBB_Static_Site (STATIC_TEMPLATE_ID,SITE_ID) values ('StaticTmpVdc_tbs_baby','TBS_BuyBuyBaby');

---- update webfooter link : BBB_CORE_PRV.BBB_FOOTER_NAVIGATION_LINKS (this data was same as existing, just created scripts as per sheet)

UPDATE BBB_CORE_PRV.BBB_FOOTER_NAVIGATION_LINKS SET FOOTER_NAME='Customer Service', CHANNEL_ID='DesktopWeb' WHERE FOOTER_LINK_ID='BBBJANPS10001';
UPDATE BBB_CORE_PRV.BBB_FOOTER_NAVIGATION_LINKS SET FOOTER_NAME='Shopping Tools', CHANNEL_ID='DesktopWeb' WHERE FOOTER_LINK_ID='BBBJANPS10002';
UPDATE BBB_CORE_PRV.BBB_FOOTER_NAVIGATION_LINKS SET FOOTER_NAME='Company Info', CHANNEL_ID='DesktopWeb' WHERE FOOTER_LINK_ID='BBBJANPS10003';
UPDATE BBB_CORE_PRV.BBB_FOOTER_NAVIGATION_LINKS SET FOOTER_NAME='Customer Service', CHANNEL_ID='DesktopWeb' WHERE FOOTER_LINK_ID='BBBJANPS10004';
UPDATE BBB_CORE_PRV.BBB_FOOTER_NAVIGATION_LINKS SET FOOTER_NAME='Shopping Tools', CHANNEL_ID='DesktopWeb' WHERE FOOTER_LINK_ID='BBBJANPS10005';
UPDATE BBB_CORE_PRV.BBB_FOOTER_NAVIGATION_LINKS SET FOOTER_NAME='Company Info', CHANNEL_ID='DesktopWeb' WHERE FOOTER_LINK_ID='BBBJANPS10006';
UPDATE BBB_CORE_PRV.BBB_FOOTER_NAVIGATION_LINKS SET FOOTER_NAME='Customer Service', CHANNEL_ID='DesktopWeb' WHERE FOOTER_LINK_ID='BBBJANPS10007';
UPDATE BBB_CORE_PRV.BBB_FOOTER_NAVIGATION_LINKS SET FOOTER_NAME='Shopping Tools', CHANNEL_ID='DesktopWeb' WHERE FOOTER_LINK_ID='BBBJANPS10008';
UPDATE BBB_CORE_PRV.BBB_FOOTER_NAVIGATION_LINKS SET FOOTER_NAME='Company Info', CHANNEL_ID='DesktopWeb' WHERE FOOTER_LINK_ID='BBBJANPS10009';

---- insert footer link : BBB_CORE_PRV.BBB_WEBFOOTER_LINKS

Insert into BBB_CORE_PRV.BBB_WEBFOOTER_LINKS (FOOTER_LINK_ID,SEQUENCE_NUM,LINK_ID) values ('BBBJANPS10009','7','DC112700001');

--- delete label template date manual : 

DELETE FROM BBB_CORE_PRV.BBB_LBL_TXT_SITE_TRANSLATIONS WHERE TRANSLATION_ID in ('PUBbbTT970001','PUBbbTT970002');
DELETE FROM BBB_CORE_PRV.BBB_LBL_TXT_TRANSLATIONS WHERE TRANSLATION_ID IN ('PUBbbTT970001','PUBbbTT970002');
DELETE FROM BBB_CORE_PRV.BBB_LBL_TXT WHERE LBL_TXT_ID LIKE 'PUBbbLTA2280001';
DELETE FROM BBB_CORE_PRV.BBB_CONFIG_KEY_VALUE WHERE CONFIG_KEY IN ('BEDBATHCAOmnitureReportSuiteId','BEDBATHCAOmnitureReportSuiteId','BEDBATHUSOmnitureReportSuiteId','BUYBUYBABYOmnitureReportSuiteId','BEDBATHUSRecipientFrom','BUYBUYBABYRecipientFrom','BEDBATHCARecipientFrom','BEDBATHUSRecipientTo','BUYBUYBABYRecipientTo','BEDBATHCARecipientTo');