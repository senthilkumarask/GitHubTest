 --ROLLBACK

  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_SITE_TRANSLATN" WHERE TRANSLATION_ID='BBB70000001' AND CONFIG_KEY_ID='200002endeca_application_name_key'; 
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_SITE_TRANSLATN" WHERE TRANSLATION_ID='BBB70000002' AND CONFIG_KEY_ID='200002endeca_application_name_key';
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_SITE_TRANSLATN" WHERE TRANSLATION_ID='BBB70000003' AND CONFIG_KEY_ID='200002endeca_application_name_key'; 
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_SITE_TRANSLATN" WHERE TRANSLATION_ID='BBB70000004' AND CONFIG_KEY_ID='200002endeca_application_name_key';
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_SITE_TRANSLATN" WHERE TRANSLATION_ID='BBB70000005' AND CONFIG_KEY_ID='200002endeca_application_name_key'; 
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_SITE_TRANSLATN" WHERE TRANSLATION_ID='BBB70000006' AND CONFIG_KEY_ID='200002endeca_application_name_key';
  
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEYS_TRANSLATIONS" WHERE TRANSLATION_ID='BBB70000001';
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEYS_TRANSLATIONS" WHERE TRANSLATION_ID='BBB70000002';
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEYS_TRANSLATIONS" WHERE TRANSLATION_ID='BBB70000003';
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEYS_TRANSLATIONS" WHERE TRANSLATION_ID='BBB70000004';
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEYS_TRANSLATIONS" WHERE TRANSLATION_ID='BBB70000005';
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEYS_TRANSLATIONS" WHERE TRANSLATION_ID='BBB70000006';
  
  DELETE FROM "BBB_CORE_PRV"."BBB_REL_CONFIG_KEY_VALUE" WHERE  CONFIG_TYPE_ID='200002' AND CONFIG_KEY_ID='200002endeca_application_name_key';

  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEY_VALUE" WHERE CONFIG_KEY_ID='200002endeca_application_name_key';
  
  COMMIT;
  
 --ROLLBACK

  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_SITE_TRANSLATN" WHERE TRANSLATION_ID='BBB70000007' AND CONFIG_KEY_ID='200002configuration_path_key'; 
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_SITE_TRANSLATN" WHERE TRANSLATION_ID='BBB70000008' AND CONFIG_KEY_ID='200002configuration_path_key'; 
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_SITE_TRANSLATN" WHERE TRANSLATION_ID='BBB70000009' AND CONFIG_KEY_ID='200002configuration_path_key'; 
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_SITE_TRANSLATN" WHERE TRANSLATION_ID='BBB700000010' AND CONFIG_KEY_ID='200002configuration_path_key'; 
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_SITE_TRANSLATN" WHERE TRANSLATION_ID='BBB700000011' AND CONFIG_KEY_ID='200002configuration_path_key'; 
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_SITE_TRANSLATN" WHERE TRANSLATION_ID='BBB700000012' AND CONFIG_KEY_ID='200002configuration_path_key'; 
  
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEYS_TRANSLATIONS" WHERE TRANSLATION_ID='BBB70000007';
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEYS_TRANSLATIONS" WHERE TRANSLATION_ID='BBB70000008';
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEYS_TRANSLATIONS" WHERE TRANSLATION_ID='BBB70000009';
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEYS_TRANSLATIONS" WHERE TRANSLATION_ID='BBB700000010';
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEYS_TRANSLATIONS" WHERE TRANSLATION_ID='BBB700000011';
  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEYS_TRANSLATIONS" WHERE TRANSLATION_ID='BBB700000012';
  
  DELETE FROM "BBB_CORE_PRV"."BBB_REL_CONFIG_KEY_VALUE" WHERE  CONFIG_TYPE_ID='200002' AND CONFIG_KEY_ID='200002configuration_path_key';

  DELETE FROM "BBB_CORE_PRV"."BBB_CONFIG_KEY_VALUE" WHERE CONFIG_KEY_ID='200002configuration_path_key';
  
  COMMIT;
